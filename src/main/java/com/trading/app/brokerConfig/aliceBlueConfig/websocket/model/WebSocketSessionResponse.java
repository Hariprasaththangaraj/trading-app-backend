package com.trading.app.brokerConfig.aliceBlueConfig.websocket.model;

import java.util.List;

public class WebSocketSessionResponse {

    private String status;
    private String message;
    private String infoMessage;
    private List<WebSocketSessionResult> result;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getInfoMessage() {
        return infoMessage;
    }

    public void setInfoMessage(String infoMessage) {
        this.infoMessage = infoMessage;
    }

    public List<WebSocketSessionResult> getResult() {
        return result;
    }

    public void setResult(List<WebSocketSessionResult> result) {
        this.result = result;
    }
}
