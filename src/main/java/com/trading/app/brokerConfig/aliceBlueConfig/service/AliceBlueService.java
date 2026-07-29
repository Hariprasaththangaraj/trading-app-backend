package com.trading.app.brokerConfig.aliceBlueConfig.service;

import com.trading.app.brokerConfig.BrokerConfig;
import org.springframework.stereotype.Service;

@Service
public class AliceBlueService {

    private final BrokerConfig brokerConfig;

    public AliceBlueService(BrokerConfig brokerConfig) {
        this.brokerConfig = brokerConfig;
    }

    public String getLoginUrl() {
        return brokerConfig.getAuthUrl()
                + "/?appcode="
                + brokerConfig.getAppKey();
    }


    public String generateAccessToken(String authCode) {
        return null;
    }
}