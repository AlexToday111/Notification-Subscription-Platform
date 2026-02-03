package com.example.notificationplatform.event.domain;


import java.util.Locale;

public class EventType {

    USER_REGISTERED("User registered"),
    PRICE_ALERT("Price alert triggered"),
    SYSTEM_MESSAGE("System message");

    private final String description;

    EventType(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }

    /**
     * Принимает:
     * "Price alert"
     * "price_alert"
     * "price-alert"
     * "PriceAlert"
     */
    public static EventType from (String raw){
        if (raw == null || raw.isBlank()){
            throw new IllegalArgumentException("EventType is blank");
        }
        String normalized = normalize(raw);
        for (EventType t : values()){
            if (t.name().equals(normalized)){
                return t;
            }
        }
        throw new IllegalArgumentException("Uknown EventType: " + raw);
    }
    private static String normalize(String raw){
        String s = raw.trim()
                .replace('-', '_')
                .replace(' ', '_')
                .toUpperCase(Locale.ROOT);
        while (s.contains("__")){
            s = s.replace("__", "_");
        }
        return s;
    }
}
