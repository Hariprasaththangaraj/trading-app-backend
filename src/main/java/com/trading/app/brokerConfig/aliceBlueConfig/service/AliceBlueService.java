package com.trading.app.brokerConfig.aliceBlueConfig.service;

import com.trading.app.brokerConfig.aliceBlueConfig.model.BorkerConfigRequest;

public interface AliceBlueService {
    String getLoginUrl(BorkerConfigRequest borkerConfigRequest) throws Exception;
    String generateAuthToken(String authCode);
}
