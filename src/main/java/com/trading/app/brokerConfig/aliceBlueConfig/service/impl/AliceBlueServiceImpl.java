package com.trading.app.brokerConfig.aliceBlueConfig.service.impl;

import com.trading.app.brokerConfig.aliceBlueConfig.model.BorkerConfigRequest;
import com.trading.app.brokerConfig.aliceBlueConfig.model.BrokerConfig;
import com.trading.app.brokerConfig.aliceBlueConfig.service.AliceBlueService;
import com.trading.app.brokerConfig.aliceBlueConfig.util.CheckSum;
import com.trading.app.brokerConfig.aliceBlueConfig.util.TokenService;
import com.trading.app.brokerConfig.aliceBlueConfig.websocket.AliceBlueWebSocket;
import com.trading.app.common.Constant;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

@Service
public class AliceBlueServiceImpl implements AliceBlueService {
    private final BrokerConfig brokerConfig;
    private final CheckSum checkSum;
    private final TokenService tokenService;
    private final AliceBlueSessionStore aliceBlueSessionStore;
    private final AliceBlueWebSocket webSocket;

    public AliceBlueServiceImpl(BrokerConfig brokerConfig, CheckSum checkSum, TokenService tokenService,
            AliceBlueSessionStore aliceBlueSessionStore, AliceBlueWebSocket webSocket) {
        this.brokerConfig = brokerConfig;
        this.checkSum = checkSum;
        this.tokenService = tokenService;
        this.aliceBlueSessionStore = aliceBlueSessionStore;
        this.webSocket = webSocket;
    }

    public String brokerConnection(BorkerConfigRequest borkerConfigRequest) throws Exception {
        if (borkerConfigRequest==null
                || borkerConfigRequest.getBrokerName()==null
                || borkerConfigRequest.getBrokerName().isBlank()
                || borkerConfigRequest.getAppKey()==null
                || borkerConfigRequest.getAppKey().isBlank()
                || borkerConfigRequest.getAppSecret()==null
                || borkerConfigRequest.getAppSecret().isBlank()
                || borkerConfigRequest.getUserId()==null) {

            throw new IllegalArgumentException(
                    "Broker Name, App Key and API Secret are required"
            );
        }

        String loginUrl =
                "https://ant.aliceblueonline.com/?appcode="
                        + UriUtils.encodeQueryParam(
                        borkerConfigRequest.getAppKey(), StandardCharsets.UTF_8);



        String checkSumToken = checkSum.getCheckSum(borkerConfigRequest.getUserId(),
                borkerConfigRequest.getAppKey(),
                borkerConfigRequest.getAppSecret());

        String sessionToken = tokenService.getSession(checkSumToken);
        // Store globally inside Spring application
        aliceBlueSessionStore.setSessionToken(sessionToken);
        //Triggering WebSocket
        if (aliceBlueSessionStore.getSessionToken()!=null) {
            webSocket.getInvalidateExistingWebSocket();
        }
        return sessionToken;
    }

    //Inserting Checksum and getting Session Token
    public String generateAuthToken(String authCode) {
        String checkSumToken = checkSum.getCheckSum(brokerConfig.getUserId(), authCode, brokerConfig.getAppSecret());
        System.out.println("Check Sum : " + checkSumToken);
        //Inserting Checksum and getting Session Token
        String sessionToken = tokenService.getSession(checkSumToken);
        // Store globally inside Spring application
        aliceBlueSessionStore.setSessionToken(sessionToken);
        //Triggering WebSocket
        if (aliceBlueSessionStore.getSessionToken()!=null) {
            webSocket.getInvalidateExistingWebSocket();
        }
        return sessionToken;
    }
}