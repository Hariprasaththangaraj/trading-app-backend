package com.trading.app.brokerConfig.aliceBlueConfig.service.impl;

import com.trading.app.brokerConfig.BrokerConfig;
import com.trading.app.brokerConfig.aliceBlueConfig.service.AliceBlueService;
import com.trading.app.brokerConfig.aliceBlueConfig.util.CheckSum;
import com.trading.app.brokerConfig.aliceBlueConfig.util.TokenService;
import org.springframework.stereotype.Service;

@Service
public class AliceBlueServiceImpl implements AliceBlueService {

    private final BrokerConfig brokerConfig;

    private final CheckSum checkSum;

    private final TokenService tokenService;

    private final AliceBlueSessionStore aliceBlueSessionStore;


    public AliceBlueServiceImpl(BrokerConfig brokerConfig, CheckSum checkSum, TokenService tokenService, AliceBlueSessionStore aliceBlueSessionStore) {
        this.brokerConfig = brokerConfig;
        this.checkSum = checkSum;
        this.tokenService = tokenService;
        this.aliceBlueSessionStore = aliceBlueSessionStore;
    }

    public String getLoginUrl() {
        return brokerConfig.getAuthUrl()
                + "/?appcode="
                + brokerConfig.getAppKey();
    }

    //Inserting Checksum and getting Session Token
    public String generateAuthToken(String authCode) {
        String checkSumToken = checkSum.getCheckSum(brokerConfig.getUserId(), authCode, brokerConfig.getAppSecret());
        System.out.println("Check Sum : " + checkSumToken);
        //Inserting Checksum and getting Session Token
        String sessionToken = tokenService.getSession(checkSumToken);
//        // Store globally inside Spring application
        aliceBlueSessionStore.setSessionToken(sessionToken);
        return sessionToken;
    }
}