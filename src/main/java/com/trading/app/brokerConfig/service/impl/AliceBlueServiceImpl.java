package com.trading.app.brokerConfig.service.impl;

import com.trading.app.brokerConfig.model.BorkerConfigRequest;
import com.trading.app.brokerConfig.model.BrokerConfig;
import com.trading.app.brokerConfig.model.BrokerConfigDTO;
import com.trading.app.brokerConfig.repo.BrokerRepo;
import com.trading.app.brokerConfig.service.AliceBlueService;
import com.trading.app.brokerConfig.util.CheckSum;
import com.trading.app.brokerConfig.util.TokenService;
import com.trading.app.brokerConfig.websocket.AliceBlueWebSocket;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

@Service
public class AliceBlueServiceImpl implements AliceBlueService {
    private final BrokerConfig brokerConfig;
    private final CheckSum checkSum;
    private final TokenService tokenService;
    private final AliceBlueSessionStore aliceBlueSessionStore;
    private final AliceBlueWebSocket webSocket;

    @Autowired
    private BrokerRepo brokerRepo;

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
            throw new IllegalArgumentException("Broker Name, App Key and API Secret are required");
        }
        return "https://ant.aliceblueonline.com/?appcode=" + UriUtils.encodeQueryParam(borkerConfigRequest.getAppKey(), StandardCharsets.UTF_8);
    }

    private String redirectionCallbackURL(BorkerConfigRequest borkerConfigRequest) {
        return "https://ant.aliceblueonline.com/?appcode=" + UriUtils.encodeQueryParam(borkerConfigRequest.getAppKey(), StandardCharsets.UTF_8);
    }

    //Inserting Checksum and getting Session Token
    public String generateAuthToken(String userId,String authCode) throws Exception {


        String checkSumToken = checkSum.getCheckSum(userId, authCode);
        System.out.println("Check Sum : " + checkSumToken);
        //Inserting Checksum and getting Session Token
        String sessionToken = tokenService.getSession(checkSumToken);
        if (sessionToken==null) {
            throw new Exception("Issue with Connecting broker !!");
        }
        // Store globally inside Spring application
        aliceBlueSessionStore.setSessionToken(sessionToken);
        //Triggering WebSocket
        if (aliceBlueSessionStore.getSessionToken()!=null) {
            webSocket.getInvalidateExistingWebSocket();
        }
        return sessionToken;
    }

    @Override
    @Transactional
    public String saveDetails(BorkerConfigRequest request) {

        // 1. Validate request
        if (request == null) {
            throw new IllegalArgumentException(
                    "Broker configuration request cannot be null");
        }

        if (request.getBrokerName() == null
                || request.getBrokerName().isBlank()) {
            throw new IllegalArgumentException("Broker Name is required");
        }

        if (request.getUserId() == null
                || request.getUserId().isBlank()) {
            throw new IllegalArgumentException("Client ID is required");
        }

        if (request.getAppKey() == null
                || request.getAppKey().isBlank()) {
            throw new IllegalArgumentException("App Key is required");
        }

        if (request.getAppSecret() == null
                || request.getAppSecret().isBlank()) {
            throw new IllegalArgumentException("API Secret is required");
        }

        // 2. Map request to entity
        BrokerConfigDTO entity = new BrokerConfigDTO();

        entity.setBrokerName(request.getBrokerName().trim());
        entity.setClientId(request.getUserId().trim());
        entity.setAppKey(request.getAppKey().trim());
        entity.setAppSecret(request.getAppSecret());
        entity.setAppId(
                request.getAppId() == null
                        || request.getAppId().isBlank()
                        ? null
                        : request.getAppId().trim()
        );

        // 3. Update existing configuration
        int updatedRows = brokerRepo.updateByClientIdAndAppKey(
                entity.getBrokerName(),
                entity.getAppSecret(),
                entity.getAppId(),
                entity.getClientId(),
                entity.getAppKey()
        );

        if (updatedRows > 0) {
            return "Broker configuration already updated.";
        }

        // 4. Insert new configuration if no matching record exists
        brokerRepo.save(entity);

        return "Broker configuration saved successfully.";
    }
}