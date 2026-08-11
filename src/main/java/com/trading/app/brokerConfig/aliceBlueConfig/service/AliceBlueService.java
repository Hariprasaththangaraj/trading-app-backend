package com.trading.app.brokerConfig.aliceBlueConfig.service;

public interface AliceBlueService {
    String getLoginUrl();
    String generateAuthToken(String authCode);
}
