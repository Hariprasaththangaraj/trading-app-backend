package com.trading.app.brokerConfig.aliceBlueConfig.websocket;

import com.trading.app.brokerConfig.BrokerConfig;
import com.trading.app.brokerConfig.aliceBlueConfig.service.impl.AliceBlueSessionStore;
import com.trading.app.brokerConfig.aliceBlueConfig.util.CheckSum;
import com.trading.app.brokerConfig.aliceBlueConfig.util.Constant;
import com.trading.app.brokerConfig.aliceBlueConfig.websocket.model.MarketDepthResponse;
import com.trading.app.brokerConfig.aliceBlueConfig.websocket.model.WebSocketConnectionRequest;
import com.trading.app.brokerConfig.aliceBlueConfig.websocket.model.WebSocketDTO;
import com.trading.app.brokerConfig.aliceBlueConfig.websocket.model.WebSocketSessionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;

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
        if (aliceBlueSessionStore.getSessionToken()!=null) {
            headers.setBearerAuth(aliceBlueSessionStore.getSessionToken());
        }
        HttpEntity<WebSocketDTO> requestEntity = new HttpEntity<>(entity, headers);
        ResponseEntity<WebSocketSessionResponse> response = restTemplate.exchange(config.getBaseUrl() + Constant.WEBSOCKET_INVALIDATE_URL, HttpMethod.POST, requestEntity, WebSocketSessionResponse.class);
        // Calling Creating Web socket
        if (response.getStatusCode()==HttpStatus.OK) {
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
        if (aliceBlueSessionStore.getSessionToken()!=null) {
            headers.setBearerAuth(aliceBlueSessionStore.getSessionToken());
        }
        HttpEntity<WebSocketDTO> requestEntity = new HttpEntity<>(entity, headers);
        ResponseEntity<WebSocketSessionResponse> rs = restTemplate.exchange(config.getBaseUrl() + Constant.WEBSOCKET_CREATE_URL, HttpMethod.POST, requestEntity, WebSocketSessionResponse.class);
        if (rs.getStatusCode()==HttpStatus.OK) {
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
                System.out.println("Payload send FAILED");
                e.printStackTrace();
            }
        }

        // This method is called automatically whenever a text message
        // is received from the AliceBlue WebSocket server.
        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message) {
            // Get the actual JSON response received from AliceBlue.
            String response = message.getPayload();
            System.out.println("AliceBlue Response = " + response);
            try {
                // Parse the received JSON response so that we can
                // read individual fields such as "t", "s", "tk", etc.
                JsonNode jsonNode = jsonValue.readTree(response);
                // First identify the message type.
                //
                // AliceBlue sends different types of WebSocket messages.
                // The "t" field tells us what kind of message we received.
                //
                // Examples:
                // "ck" = Authentication / connection response
                // "dk" = Initial depth acknowledgement/response
                // "df" = Live depth feed
                String type = jsonNode.get("t").asText();
                // =========================================================
                // 1. AUTHENTICATION RESPONSE
                // =========================================================
                //
                // "ck" means that AliceBlue is responding to our
                // WebSocket authentication/connection request.
                //
                // We check this first because we should subscribe to
                // market data only after authentication is successful.
                if ("ck".equals(type)) {
                    // Convert the JSON authentication response into
                    // our Java WebSocketConnectionRequest object.
                    WebSocketConnectionRequest wsResponse = jsonValue.readValue(response, WebSocketConnectionRequest.class);
                    // Check whether AliceBlue authentication was successful.
                    //
                    // "OK" means authentication was successful.
                    // Only after this do we start our market-data subscription.
                    if ("OK".equals(wsResponse.getS())) {
                        System.out.println(">>> Authentication SUCCESS");
                        // Authentication is complete.
                        // Now send our market-data subscription payload(s)
                        // to AliceBlue through the same WebSocket session.
                        subscribeDepth(session);
                    }
                }
                // =========================================================
                // 2. INITIAL DEPTH RESPONSE
                // =========================================================
                //
                // "dk" indicates the initial response/acknowledgement
                // related to our depth subscription.
                //
                // This tells us that AliceBlue has responded to our
                // market-depth subscription request.
                else if ("dk".equals(type)) {
                    // Convert the JSON depth response into our
                    // MarketDepthResponse Java object.
                    MarketDepthResponse depthResponse = jsonValue.readValue(response, MarketDepthResponse.class);
                    System.out.println(">>> Market Depth Acknowledgement");
                    // Display the Last Traded Price received from AliceBlue.
                    System.out.println("LTP : " + depthResponse.getLastTradedPrice());
                    // Display the instrument token so that we know
                    // which instrument this depth response belongs to.
                    System.out.println("Token : " + depthResponse.getToken());
                }
                // =========================================================
                // 3. LIVE DEPTH FEED
                // =========================================================
                //
                // "df" indicates that this is a live market-depth update.
                //
                // After subscription, AliceBlue continuously sends
                // updated market data through the WebSocket.
                //
                // Every time a new depth update arrives,
                // this block will be executed.
                else if ("df".equals(type)) {
                    // Convert the live JSON depth-feed response into
                    // our MarketDepthResponse Java object.
                    MarketDepthResponse depthResponse = jsonValue.readValue(response, MarketDepthResponse.class);
                    System.out.println(">>> Market Depth Feed");
                    // Display the latest traded price from the live feed.
                    System.out.println("LTP : " + depthResponse.getLastTradedPrice());
                    // Display the token to identify which instrument
                    // generated this live depth update.
                    System.out.println("Token : " + depthResponse.getToken());
                }
            } catch (Exception e) {
                // If JSON parsing, object conversion, or any other
                // message-processing operation fails, we come here.
                System.out.println("Error processing AliceBlue WebSocket response");
                e.printStackTrace();
            }
        }

        @Override
        public void handleTransportError(WebSocketSession session, Throwable exception) {
            exception.printStackTrace();
        }
    };
    StandardWebSocketClient webSocketClient = new StandardWebSocketClient();

    //Initiating Websocket Connection
    public void connectWebSocket() {
        webSocketClient.execute(handler, WEBSOCKET_URL);
        System.out.println("WebSocket connection initiated...");
    }

    /**
     * @param session
     */
    private void subscribeDepth(WebSocketSession session) {
        try {
            ClassPathResource resource = new ClassPathResource("subscriptions/subscription.json");
            // Read the JSON file content
            String payload = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            // Convert the JSON file into a JSON Array
            JsonNode root = jsonValue.readTree(payload);

            // Each element represents one subscription payload
            for (JsonNode payloadNode : root) {
                // Convert one JSON object into a JSON string
                String payloads = payloadNode.toString();
                System.out.println("Sending Subscription: " + payloads);
                // Send this subscription as a separate
                // WebSocket text message
                TextMessage message = new TextMessage(payloads);
                session.sendMessage(message);
            }
            System.out.println(">>> All Subscriptions Sent Successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
