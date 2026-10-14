package com.trading.app.brokerConfig.service;

import com.trading.app.brokerConfig.model.BorkerConfigRequest;

public interface AliceBlueService {
    String brokerConnection(BorkerConfigRequest borkerConfigRequest) throws Exception;
    String generateAuthToken(String authCode) throws Exception;
    String saveDetails(BorkerConfigRequest borkerConfigRequest);
}
