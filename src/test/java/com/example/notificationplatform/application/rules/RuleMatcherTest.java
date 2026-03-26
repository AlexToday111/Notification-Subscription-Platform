package com.example.notificationplatform.application.rules;

import com.example.notificationplatform.domain.event.EventType;
import com.example.notificationplatform.domain.subscription.Channel;
import com.example.notificationplatform.domain.subscription.Subscription;
import com.example.notificationplatform.domain.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RuleMatcherTest {

    private final RuleMatcher matcher = new RuleMatcher(new ObjectMapper());

    @Test
    void matches_allConditionsAndPayloadPaths() {
        Subscription subscription = subscription("""
                {"all":[
                  {"field":"severity","op":"eq","value":"CRITICAL"},
                  {"field":"payload.order.amount","op":"gte","value":100},
                  {"field":"service","op":"in","value":["billing","auth"]}
                ]}
                """);

        assertTrue(matcher.matches(subscription, """
                {"severity":"CRITICAL","service":"billing","order":{"amount":120}}
                """));
    }

    @Test
    void matches_missingFieldIsPredictablyFalse() {
        Subscription subscription = subscription("""
                {"field":"payload.user.id","op":"exists"}
                """);

        assertFalse(matcher.matches(subscription, "{\"user\":{}}"));
    }

    @Test
    void matches_invalidRuleReturnsFalse() {
        Subscription subscription = subscription("""
                {"field":"severity","op":"unsupported","value":"CRITICAL"}
                """);

        assertFalse(matcher.matches(subscription, "{\"severity\":\"CRITICAL\"}"));
    }

    private Subscription subscription(String conditionJson) {
        return new Subscription(
                new User("rule@example.com", "Rule User"),
                EventType.SYSTEM_MESSAGE,
                Channel.EMAIL,
                "rule@example.com",
                conditionJson
        );
    }
}
