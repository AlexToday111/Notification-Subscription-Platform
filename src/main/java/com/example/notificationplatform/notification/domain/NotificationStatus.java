package com.example.notificationplatform.notification.domain;

public enum NotificationStatus{
    NEW,
    SENDING,
    SENT,
    FAILED;

    public boolean isTerminal(){
        return this == SENT || this == FAILED;
    }
}