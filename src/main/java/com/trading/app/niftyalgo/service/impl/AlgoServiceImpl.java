package com.trading.app.niftyalgo.service.impl;

import com.trading.app.brokerConfig.websocket.model.MarketDepthResponse;
import com.trading.app.dashbardcontroller.model.MarketDataDTO;
import com.trading.app.datapublisher.MarketDataEvent;
import com.trading.app.niftyalgo.service.AlgoService;
import org.springframework.context.event.EventListener;

public class AlgoServiceImpl implements AlgoService {

    private MarketDepthResponse response;

    @EventListener
    public void handleMarketData(MarketDataEvent event) {
        this.response = event.response();
        System.out.println("Received Token: " + this.response.getToken());
    }

    public MarketDataDTO getNiftyFutureLtp() {

        // Check whether market data has been received
        if (this.response == null) {
            System.out.println("Market data is not received yet");
            return null;
        }

        // Create DTO
        MarketDataDTO dto = new MarketDataDTO();

        // Map values from MarketDepthResponse
        dto.setLtp(this.response.getLastTradedPrice());
        dto.setToken(this.response.getToken());
        dto.setPercentageChange(this.response.getPercentageChange());

        return dto;
    }

}
