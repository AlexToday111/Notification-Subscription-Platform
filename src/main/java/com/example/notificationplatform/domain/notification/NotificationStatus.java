package com.example.notificationplatform.domain.notification;

public enum NotificationStatus{
    PENDING,
    QUEUED,
    NEW,
    SENDING,
    SENT,
    RETRYING,
    RETRY_SCHEDULED,
    FAILED,
    DEAD_LETTERED;

    public boolean isTerminal(){
        return this == SENT || this == FAILED || this == DEAD_LETTERED;
    }
}
