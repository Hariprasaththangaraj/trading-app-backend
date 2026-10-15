package com.trading.app.brokerConfig.service;

import com.trading.app.brokerConfig.model.BorkerConfigRequest;

public interface BrokerService {
    String brokerConnection(BorkerConfigRequest borkerConfigRequest) throws Exception;

    String saveDetails(BorkerConfigRequest borkerConfigRequest);

    String generateAuthToken(String userId, String authCode) throws Exception;

    BorkerConfigRequest getBrokerDetails(Long userId, String brokerName);

    BorkerConfigRequest getBrokerDeatilsByUserId(Long userId);
}
