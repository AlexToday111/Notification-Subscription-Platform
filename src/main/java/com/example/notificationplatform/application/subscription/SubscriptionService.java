package com.example.notificationplatform.application.subscription;

import com.example.notificationplatform.application.audit.AuditService;
import com.example.notificationplatform.application.rules.RuleMatcher;
import com.example.notificationplatform.domain.subscription.Subscription;
import com.example.notificationplatform.infrastructure.persistence.subscription.SubscriptionRepository;
import com.example.notificationplatform.application.subscription.command.CreateSubscriptionCommand;
import com.example.notificationplatform.application.exception.NotFoundException;
import com.example.notificationplatform.domain.user.User;
import com.example.notificationplatform.infrastructure.persistence.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SubscriptionService {

    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final RuleMatcher ruleMatcher;
    private final AuditService auditService;

    public SubscriptionService(UserRepository userRepository,
                               SubscriptionRepository subscriptionRepository,
                               RuleMatcher ruleMatcher,
                               AuditService auditService) {
        this.userRepository = userRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.ruleMatcher = ruleMatcher;
        this.auditService = auditService;
    }

    @Transactional
    public Subscription create(CreateSubscriptionCommand cmd) {
        if (cmd == null) throw new IllegalArgumentException("command is null");
        if (cmd.userId() == null) throw new IllegalArgumentException("userId is null");
        if (cmd.eventType() == null) throw new IllegalArgumentException("eventType is null");
        if (cmd.channel() == null) throw new IllegalArgumentException("channel is null");
        if (cmd.destination() == null || cmd.destination().isBlank()) throw new IllegalArgumentException("destination is blank");

        User user = userRepository.findById(cmd.userId())
                .orElseThrow(() -> new NotFoundException("User not found: " + cmd.userId()));

        String destination = cmd.destination().trim();
        if (!cmd.channel().isValidDestination(destination)) {
            throw new IllegalArgumentException("Invalid destination for channel " + cmd.channel());
        }
        ruleMatcher.validate(cmd.conditionJson());

        Subscription entity = new Subscription(
                user,
                cmd.eventType(),
                cmd.channel(),
                destination,
                cmd.conditionJson()
        );

        Subscription saved = subscriptionRepository.save(entity);
        auditService.record("SUBSCRIPTION_CREATED", "Subscription", saved.getId().toString(), "eventType=" + saved.getEventType());
        return saved;
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Subscription> listByUser(UUID userId) {
        if (userId == null) throw new IllegalArgumentException("userId is null");
        return subscriptionRepository.findByUser_IdOrderByCreatedAtDesc(userId);
    }
}
