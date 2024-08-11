package com.pj.planjourney.domain.notification.dto;

public class NotificationMessage {
    private String content;

    public NotificationMessage(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
