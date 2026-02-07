package com.example.notificationplatform.domain.notification;

public enum NotificationStatus{
    NEW,
    SENDING,
    SENT,
    RETRYING,
    FAILED;

    public boolean isTerminal(){
        return this == SENT || this == FAILED;
    }
}