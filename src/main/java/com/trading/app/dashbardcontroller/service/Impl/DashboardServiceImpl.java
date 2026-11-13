package com.trading.app.dashbardcontroller.service.Impl;

import com.trading.app.brokerConfig.websocket.model.MarketDepthResponse;
import com.trading.app.dashbardcontroller.model.MarketDataDTO;
import com.trading.app.dashbardcontroller.service.DashboardService;
import com.trading.app.datapublisher.MarketDataEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImpl implements DashboardService {

    private MarketDepthResponse response;

    @EventListener
    public void handleMarketData(MarketDataEvent event) {

        this.response = event.response();
    }

    @Override
    public MarketDataDTO getCrudeOilLtp() {

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