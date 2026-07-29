package com.trading.app.brokerConfig.aliceBlueConfig.util;

import org.springframework.stereotype.Service;

@Service
public class TokenStore {
    private String accessToken;

    public void saveToken(String token){
        this.accessToken = token;
    }

    public String getToken(){
        return accessToken;
    }
}
