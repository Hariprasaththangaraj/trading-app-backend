package com.trading.app.brokerConfig.service.impl;

import com.trading.app.brokerConfig.model.BorkerConfigRequest;
import com.trading.app.brokerConfig.model.BrokerConfig;
import com.trading.app.brokerConfig.model.BrokerConfigDTO;
import com.trading.app.brokerConfig.repo.BrokerRepo;
import com.trading.app.brokerConfig.service.BrokerService;
import com.trading.app.brokerConfig.util.CheckSum;
import com.trading.app.brokerConfig.util.TokenService;
import com.trading.app.brokerConfig.websocket.AliceBlueWebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

@Service
public class BrokerServiceImpl implements BrokerService {
    private final BrokerConfig brokerConfig;
    private final CheckSum checkSum;
    private final TokenService tokenService;
    private final AliceBlueSessionStore aliceBlueSessionStore;
    private final AliceBlueWebSocket webSocket;
    @Autowired
    private BrokerRepo brokerRepo;

    public BrokerServiceImpl(BrokerConfig brokerConfig, CheckSum checkSum, TokenService tokenService,
            AliceBlueSessionStore aliceBlueSessionStore, AliceBlueWebSocket webSocket) {
        this.brokerConfig = brokerConfig;
        this.checkSum = checkSum;
        this.tokenService = tokenService;
        this.aliceBlueSessionStore = aliceBlueSessionStore;
        this.webSocket = webSocket;
    }

    public String brokerConnection(BorkerConfigRequest borkerConfigRequest) throws Exception {
        if (borkerConfigRequest==null || borkerConfigRequest.getBrokerName()==null || borkerConfigRequest.getBrokerName().isBlank() || borkerConfigRequest.getAppKey()==null || borkerConfigRequest.getAppKey().isBlank() || borkerConfigRequest.getAppSecret()==null || borkerConfigRequest.getAppSecret().isBlank() || borkerConfigRequest.getClientId()==null) {
            throw new IllegalArgumentException("Broker Name, App Key and API Secret are required");
        }
        return "https://ant.aliceblueonline.com/?appcode=" + UriUtils.encodeQueryParam(borkerConfigRequest.getAppKey(), StandardCharsets.UTF_8);
    }

    private String redirectionCallbackURL(BorkerConfigRequest borkerConfigRequest) {
        return "https://ant.aliceblueonline.com/?appcode=" + UriUtils.encodeQueryParam(borkerConfigRequest.getAppKey(), StandardCharsets.UTF_8);
    }

    //Inserting Checksum and getting Session Token
    public String generateAuthToken(String userId, String authCode) throws Exception {
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
    @Transactional(readOnly = true)
    public BorkerConfigRequest getBrokerDetails(Long userId, String brokerName) {
        if (userId==null || userId <= 0) {
            throw new IllegalArgumentException("Valid TradingApp User ID is required");
        }
        if (brokerName==null || brokerName.isBlank()) {
            throw new IllegalArgumentException("Broker Name is required");
        }
        BrokerConfigDTO entity = brokerRepo.findByUserIdAndBrokerName(userId, brokerName.trim()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Broker configuration not found"));
        BorkerConfigRequest response = new BorkerConfigRequest();
        response.setBrokerName(entity.getBrokerName());
        response.setClientId(entity.getClientId());
        response.setAppKey(entity.getAppKey());
        response.setAppSecret(entity.getAppSecret());
        response.setAppId(entity.getAppId());
        response.setUserId(entity.getUserId());
        return response;
    }

    @Override
    @Transactional
    public String saveDetails(BorkerConfigRequest request) {
        // 1. Validate request
        if (request==null) {
            throw new IllegalArgumentException("Broker configuration request cannot be null");
        }
        if (request.getBrokerName()==null || request.getBrokerName().isBlank()) {
            throw new IllegalArgumentException("Broker Name is required");
        }
        if (request.getClientId()==null || request.getClientId().isBlank()) {
            throw new IllegalArgumentException("Client ID is required");
        }
        if (request.getAppKey()==null || request.getAppKey().isBlank()) {
            throw new IllegalArgumentException("App Key is required");
        }
        if (request.getAppSecret()==null || request.getAppSecret().isBlank()) {
            throw new IllegalArgumentException("API Secret is required");
        }
        // 2. Map request to entity
        BrokerConfigDTO entity = new BrokerConfigDTO();
        entity.setBrokerName(request.getBrokerName().trim());
        entity.setClientId(request.getClientId().trim());
        entity.setAppKey(request.getAppKey().trim());
        entity.setAppSecret(request.getAppSecret());
        entity.setAppId(request.getAppId()==null || request.getAppId().isBlank() ? null:request.getAppId().trim());
        // 3. Update existing configuration
        int updatedRows = brokerRepo.updateByClientIdAndAppKey(entity.getBrokerName(), entity.getAppSecret(), entity.getAppId(), entity.getClientId(), entity.getAppKey());
        if (updatedRows > 0) {
            return "Broker configuration already updated.";
        }
        // 4. Insert new configuration if no matching record exists
        brokerRepo.save(entity);
        return "Broker configuration saved successfully.";
    }

    @Transactional(readOnly = true)
    public BorkerConfigRequest getBrokerDeatilsByUserId(Long userId) {
        BrokerConfigDTO entity = brokerRepo.findByUserId(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Broker configuration not found"));
        BorkerConfigRequest configRequest = new BorkerConfigRequest();
        configRequest.setUserId(entity.getUserId());
        configRequest.setBrokerName(entity.getBrokerName());
        configRequest.setClientId(entity.getClientId());
        configRequest.setAppId(entity.getAppId());
        configRequest.setAppKey(entity.getAppKey());
        return configRequest;
    }
}