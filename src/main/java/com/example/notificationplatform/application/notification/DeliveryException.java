package com.example.notificationplatform.application.notification;

public class DeliveryException extends RuntimeException {
    private final String code;
    private final Integer responseStatus;
    private final String diagnostic;
    private final boolean retryable;

    public DeliveryException(String code, String message, Integer responseStatus, String diagnostic, boolean retryable) {
        super(message);
        this.code = code;
        this.responseStatus = responseStatus;
        this.diagnostic = diagnostic;
        this.retryable = retryable;
    }

    public String code() {
        return code;
    }

    public Integer responseStatus() {
        return responseStatus;
    }

    public String diagnostic() {
        return diagnostic;
    }

    public boolean retryable() {
        return retryable;
    }
}
