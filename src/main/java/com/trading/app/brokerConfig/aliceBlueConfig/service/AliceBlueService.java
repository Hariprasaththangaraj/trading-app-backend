package com.trading.app.brokerConfig.aliceBlueConfig.service;

import com.trading.app.brokerConfig.BrokerConfig;
import com.trading.app.brokerConfig.aliceBlueConfig.model.VendorLoginResponse;
import com.trading.app.brokerConfig.aliceBlueConfig.util.CheckSum;
import com.trading.app.brokerConfig.aliceBlueConfig.util.TokenService;
import org.springframework.stereotype.Service;

@Service
public class AliceBlueService {

    private final BrokerConfig brokerConfig;

    private final CheckSum checkSum;

    private final TokenService tokenService;


    public AliceBlueService(BrokerConfig brokerConfig, CheckSum checkSum, TokenService tokenService) {
        this.brokerConfig = brokerConfig;
        this.checkSum = checkSum;
        this.tokenService = tokenService;
    }

    public String getLoginUrl() {
        return brokerConfig.getAuthUrl()
                + "/?appcode="
                + brokerConfig.getAppKey();
    }


    public String generateCheckSum(String authCode) {
        String checkSumToken = checkSum.getCheckSum(brokerConfig.getUserId(), authCode, brokerConfig.getAppSecret());
        System.out.println("Check Sum : " + checkSumToken);
        tokenService.getSession(checkSumToken);
        return checkSumToken;
    }
}