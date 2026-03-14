package com.example.notificationplatform.infrastructure.notification;

import com.example.notificationplatform.domain.notification.Notification;
import com.example.notificationplatform.domain.subscription.Channel;
import com.example.notificationplatform.application.notification.DeliveryException;
import com.example.notificationplatform.application.notification.DeliveryResult;
import com.example.notificationplatform.application.notification.NotificationSender;
import lombok.extern.slf4j.Slf4j;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Map;

@Slf4j
@Component
public class WebhookNotificationSender implements NotificationSender{
    private final RestClient restClient;
    private final String secret;
    private final MeterRegistry meterRegistry;

    public WebhookNotificationSender(RestClient.Builder builder,
                                     @Value("${app.channels.webhook.secret:local-webhook-secret}") String secret,
                                     MeterRegistry meterRegistry) {
        this.restClient = builder.build();
        this.secret = secret;
        this.meterRegistry = meterRegistry;
    }

    @Override
    public Channel channel() {
        return Channel.WEBHOOK;
    }

    @Override
    public DeliveryResult send(Notification notification) {
        String timestamp = String.valueOf(Instant.now().getEpochSecond());
        Map<String, Object> body = Map.of(
                "notificationId", notification.getId().toString(),
                "eventId", notification.getEvent().getId().toString(),
                "type", notification.getEvent().getType().name(),
                "content", notification.getContent(),
                "correlationId", notification.getCorrelationId() == null ? "" : notification.getCorrelationId()
        );
        String canonical = timestamp + "." + notification.getId() + "." + notification.getContent();
        try {
            String responseBody = restClient.post()
                    .uri(notification.getDestination())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-Notification-Timestamp", timestamp)
                    .header("X-Notification-Signature", hmac(canonical))
                    .header("X-Correlation-Id", notification.getCorrelationId() == null ? "" : notification.getCorrelationId())
                    .body(body)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                        throw new DeliveryException("WEBHOOK_4XX", "Webhook endpoint rejected request", response.getStatusCode().value(), "client_error", false);
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                        throw new DeliveryException("WEBHOOK_5XX", "Webhook endpoint failed", response.getStatusCode().value(), "server_error", true);
                    })
                    .body(String.class);
            log.info("Webhook delivery result notificationId={} channel={} status={}",
                    notification.getId(), notification.getChannel(), 200);
            meterRegistry.counter("webhook.delivery.status", "status", "200").increment();
            return new DeliveryResult(200, responseBody == null ? "ok" : responseBody.substring(0, Math.min(responseBody.length(), 200)));
        } catch (DeliveryException e) {
            throw e;
        } catch (RestClientException e) {
            throw new DeliveryException("WEBHOOK_NETWORK", e.getMessage(), null, e.getClass().getSimpleName(), true);
        }
    }

    private String hmac(String value) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return "sha256=" + HexFormat.of().formatHex(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Cannot sign webhook payload", e);
        }
    }
}
