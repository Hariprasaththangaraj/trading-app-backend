package com.trading.app.brokerConfig.aliceBlueConfig.util;

import com.trading.app.brokerConfig.aliceBlueConfig.model.VendorLoginRequest;
import com.trading.app.brokerConfig.aliceBlueConfig.model.VendorLoginResponse;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.ObjectMapper;

@Service
public class TokenService {

    private final RestTemplate restTemplate;

    public TokenService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getSession(String checksum) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        VendorLoginRequest request =
                new VendorLoginRequest(checksum);

        HttpEntity<VendorLoginRequest> entity =
                new HttpEntity<>(request, headers);


        ObjectMapper mapper = new ObjectMapper();

        System.out.println(
                mapper.writeValueAsString(request)
        );

        ResponseEntity<VendorLoginResponse> response =
                restTemplate.exchange(
                        Constant.CHECKSUM_URL,
                        HttpMethod.POST,
                        entity,
                        VendorLoginResponse.class
                );

        System.out.print("Auth Token : " + response.getBody().getUserSession());
        return response.getBody().getUserSession();

    }
}
