package com.trading.app.brokerConfig.aliceBlueConfig.websocket.model;

import org.springframework.beans.factory.annotation.Value;

public class WebSocketDTO {
    private String source;

    @Value("${user-id}")
    private String userId;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
