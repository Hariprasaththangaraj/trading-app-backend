package com.trading.app.brokerConfig.util;

import com.trading.app.brokerConfig.model.BrokerConfigDTO;
import com.trading.app.brokerConfig.repo.BrokerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Component
public class CheckSum {

    @Autowired
    BrokerRepo brokerRepo;

    public String getCheckSum(String userId, String authCode) {
        try {
            if (userId==null || authCode==null) {
                throw new RuntimeException("Cannot create Session Token !!!");
            }
            BrokerConfigDTO userDetails = brokerRepo.getByClientId(userId);
            String secret = userDetails.getAppSecret();
            String data = userId + authCode + secret;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String sha256(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}