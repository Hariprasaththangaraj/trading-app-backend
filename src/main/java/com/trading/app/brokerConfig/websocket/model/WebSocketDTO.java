package com.trading.app.brokerConfig.websocket.model;

public class WebSocketDTO {
    private String source;

    private String clientId;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
