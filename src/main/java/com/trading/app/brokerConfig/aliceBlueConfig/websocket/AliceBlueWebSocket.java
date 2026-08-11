package com.trading.app.brokerConfig.aliceBlueConfig.websocket;

import com.trading.app.brokerConfig.BrokerConfig;
import com.trading.app.brokerConfig.aliceBlueConfig.service.impl.AliceBlueSessionStore;
import com.trading.app.brokerConfig.aliceBlueConfig.util.CheckSum;
import com.trading.app.brokerConfig.aliceBlueConfig.util.Constant;
import com.trading.app.brokerConfig.aliceBlueConfig.websocket.model.WebSocketConnectionRequest;
import com.trading.app.brokerConfig.aliceBlueConfig.websocket.model.WebSocketDTO;
import com.trading.app.brokerConfig.aliceBlueConfig.websocket.model.WebSocketSessionResponse;
import org.java_websocket.client.WebSocketClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.databind.ObjectMapper;

import static com.trading.app.brokerConfig.aliceBlueConfig.util.Constant.WEBSOCKET_URL;

@Component
public class AliceBlueWebSocket {

    private final RestTemplate restTemplate;

    @Autowired
    BrokerConfig config;

    private final AliceBlueSessionStore aliceBlueSessionStore;


    @Autowired
    CheckSum checkSum;


    public AliceBlueWebSocket(RestTemplate restTemplate, AliceBlueSessionStore aliceBlueSessionStore) {
        this.restTemplate = restTemplate;
        this.aliceBlueSessionStore = aliceBlueSessionStore;
    }

    //Terminating or invalidating existing websocket
    public void getInvalidateExistingWebSocket() {
        System.out.println("START : Invalidating Web Socket");

        WebSocketDTO entity = new WebSocketDTO();
        entity.setSource("API");
        entity.setUserId(config.getUserId());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        if (aliceBlueSessionStore.getSessionToken() != null) {
            headers.setBearerAuth(aliceBlueSessionStore.getSessionToken());
        }
        HttpEntity<WebSocketDTO> requestEntity =
                new HttpEntity<>(entity, headers);

        ResponseEntity<WebSocketSessionResponse> response = restTemplate.exchange(
                config.getBaseUrl() + Constant.WEBSOCKET_INVALIDATE_URL,
                HttpMethod.POST,
                requestEntity,
                WebSocketSessionResponse.class
        );

        // Calling Creating Web socket
        if (response.getStatusCode() == HttpStatus.OK) {
            createWebSocket();
        }
        System.out.println("END : Invalidating Web Socket");
    }

    //Create Session
    public void createWebSocket() {
        System.out.println("START : Creating Web Socket Session Create!!!");

        WebSocketDTO entity = new WebSocketDTO();
        entity.setSource("API");
        entity.setUserId(config.getUserId());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        if (aliceBlueSessionStore.getSessionToken() != null) {
            headers.setBearerAuth(aliceBlueSessionStore.getSessionToken());
        }
        HttpEntity<WebSocketDTO> requestEntity =
                new HttpEntity<>(entity, headers);

        ResponseEntity<WebSocketSessionResponse> rs = restTemplate.exchange(
                config.getBaseUrl() + Constant.WEBSOCKET_CREATE_URL,
                HttpMethod.POST,
                requestEntity,
                WebSocketSessionResponse.class
        );

        if (rs.getStatusCode() == HttpStatus.OK) {
            connectWebSocket();
        }

        System.out.println("END : Creating Web Socket Session Create!!!");
    }

    ObjectMapper jsonValue = new ObjectMapper();

    private String creatingPayload() {
        WebSocketConnectionRequest wSCRequest = new WebSocketConnectionRequest();
        //Create 2 time encrypted key
        String getTwiceSHAKey = getEncryptedKeyTwice();
        String clientID = config.getUserId() + "_API";

        wSCRequest.setSusertoken(getTwiceSHAKey);
        wSCRequest.setT("c");
        wSCRequest.setActid(clientID);
        wSCRequest.setUid(clientID);
        wSCRequest.setSource("API");

        return jsonValue.writeValueAsString(wSCRequest);
    }

    private String getEncryptedKeyTwice() {
        String key = checkSum.sha256(aliceBlueSessionStore.getSessionToken());
        return checkSum.sha256(key);
    }


    //Create connection
    TextWebSocketHandler handler = new TextWebSocketHandler() {

        @Override
        public void afterConnectionEstablished(WebSocketSession session) {
            System.out.println("WebSocket Connected!");

            try {

                String payload = creatingPayload();

                System.out.println("Sending Payload: " + payload);

                session.sendMessage(new TextMessage(payload));

                System.out.println(">>> Payload Sent Successfully");
            } catch (Exception e) {
                System.out.println(
                        "Payload send FAILED"
                );

                e.printStackTrace();
            }
        }

        // Here we need to send our payload
        @Override
        protected void handleTextMessage(
                WebSocketSession session,
                TextMessage message) {

            String response =
                    message.getPayload();

            System.out.println(
                    "AliceBlue Response = "
                            + response
            );

            if (response.contains("\"t\":\"ck\"")
                    && response.contains("\"s\":\"OK\"")) {

                System.out.println(
                        ">>> Authentication SUCCESS"
                );

                subscribeDepth(session);
            }
        }

        @Override
        public void handleTransportError(
                WebSocketSession session,
                Throwable exception) {

            exception.printStackTrace();
        }

    };

    StandardWebSocketClient webSocketClient = new StandardWebSocketClient();

    //Initiating Websocket Connection
    public void connectWebSocket() {
        webSocketClient.execute(
                handler,
                WEBSOCKET_URL
        );

        System.out.println(
                "WebSocket connection initiated..."
        );
    }

    private void subscribeDepth(
            WebSocketSession session) {

        try {

            String payload ="{\"k\":\"NFO|48704\",\"t\":\"d\"}";

            System.out.println(
                    "Sending Subscription: "
                            + payload
            );

            session.sendMessage(
                    new TextMessage(payload)
            );

            System.out.println(
                    ">>> Subscription Sent Successfully"
            );

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

}
