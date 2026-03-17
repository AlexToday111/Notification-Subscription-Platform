package com.example.notificationplatform.application.rules;

import com.example.notificationplatform.domain.subscription.Subscription;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Iterator;

@Component
@RequiredArgsConstructor
public class RuleMatcher {

    private final ObjectMapper objectMapper;

    public boolean matches(Subscription subscription, String payload) {
        String condition = subscription.getConditionJson();
        if (condition == null || condition.isBlank()) {
            return true;
        }
        try {
            JsonNode rule = objectMapper.readTree(condition);
            JsonNode payloadNode = objectMapper.readTree(payload);
            validate(rule);
            return evaluate(rule, payloadNode);
        } catch (Exception ignored) {
            return false;
        }
    }

    public void validate(String conditionJson) {
        if (conditionJson == null || conditionJson.isBlank()) {
            return;
        }
        try {
            validate(objectMapper.readTree(conditionJson));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid subscription condition JSON", e);
        }
    }

    private void validate(JsonNode rule) {
        if (rule.has("all")) {
            requireArray(rule.get("all"), "all");
            rule.get("all").forEach(this::validate);
            return;
        }
        if (rule.has("any")) {
            requireArray(rule.get("any"), "any");
            rule.get("any").forEach(this::validate);
            return;
        }
        if (!rule.hasNonNull("field") || !rule.hasNonNull("op")) {
            throw new IllegalArgumentException("Rule leaf must contain field and op");
        }
        String op = rule.get("op").asText();
        if (!("eq".equals(op) || "ne".equals(op) || "gt".equals(op) || "gte".equals(op)
                || "lt".equals(op) || "lte".equals(op) || "in".equals(op) || "exists".equals(op))) {
            throw new IllegalArgumentException("Unsupported rule operator: " + op);
        }
        if (!"exists".equals(op) && !rule.has("value")) {
            throw new IllegalArgumentException("Rule operator requires value: " + op);
        }
    }

    private void requireArray(JsonNode node, String field) {
        if (node == null || !node.isArray()) {
            throw new IllegalArgumentException(field + " must be an array");
        }
    }

    private boolean evaluate(JsonNode rule, JsonNode payload) {
        if (rule.has("all")) {
            for (JsonNode child : rule.get("all")) {
                if (!evaluate(child, payload)) {
                    return false;
                }
            }
            return true;
        }
        if (rule.has("any")) {
            for (JsonNode child : rule.get("any")) {
                if (evaluate(child, payload)) {
                    return true;
                }
            }
            return false;
        }
        JsonNode actual = path(payload, rule.get("field").asText());
        String op = rule.get("op").asText();
        if ("exists".equals(op)) {
            return actual != null && !actual.isMissingNode() && !actual.isNull();
        }
        if (actual == null || actual.isMissingNode() || actual.isNull()) {
            return false;
        }
        JsonNode expected = rule.get("value");
        return switch (op) {
            case "eq" -> comparable(actual, expected) == 0;
            case "ne" -> comparable(actual, expected) != 0;
            case "gt" -> comparable(actual, expected) > 0;
            case "gte" -> comparable(actual, expected) >= 0;
            case "lt" -> comparable(actual, expected) < 0;
            case "lte" -> comparable(actual, expected) <= 0;
            case "in" -> contains(expected, actual);
            default -> false;
        };
    }

    private JsonNode path(JsonNode root, String dottedPath) {
        JsonNode current = root;
        String normalized = dottedPath.startsWith("payload.") ? dottedPath.substring("payload.".length()) : dottedPath;
        for (String part : normalized.split("\\.")) {
            if (current == null || current.isMissingNode() || current.isNull()) {
                return null;
            }
            current = current.get(part);
        }
        return current;
    }

    private int comparable(JsonNode actual, JsonNode expected) {
        if (actual.isNumber() && expected.isNumber()) {
            return actual.decimalValue().compareTo(expected.decimalValue());
        }
        if (actual.isNumber() && expected.isTextual()) {
            return actual.decimalValue().compareTo(new BigDecimal(expected.asText()));
        }
        if (actual.isTextual() && expected.isNumber()) {
            return new BigDecimal(actual.asText()).compareTo(expected.decimalValue());
        }
        return actual.asText().compareTo(expected.asText());
    }

    private boolean contains(JsonNode expectedArray, JsonNode actual) {
        if (!expectedArray.isArray()) {
            return false;
        }
        Iterator<JsonNode> values = expectedArray.elements();
        while (values.hasNext()) {
            if (comparable(actual, values.next()) == 0) {
                return true;
            }
        }
        return false;
    }
}
