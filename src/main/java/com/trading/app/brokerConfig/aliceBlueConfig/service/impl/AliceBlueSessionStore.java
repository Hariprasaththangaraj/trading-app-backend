package com.trading.app.brokerConfig.aliceBlueConfig.service.impl;

import org.springframework.stereotype.Component;

@Component
public class AliceBlueSessionStore {

    private volatile String sessionToken;

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public String getSessionToken() {
        if (sessionToken == null || sessionToken.isBlank()) {
            throw new IllegalStateException(
                    "AliceBlue session token is not available"
            );
        }

        return sessionToken;
    }

    public boolean isAvailable() {
        return sessionToken != null
                && !sessionToken.isBlank();
    }

    public void clear() {
        this.sessionToken = null;
    }
}