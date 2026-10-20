package com.trading.app.datapublisher.impl;

 import com.trading.app.brokerConfig.websocket.model.MarketDepthResponse;
 import com.trading.app.datapublisher.MarketDataEvent;
 import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class CommonMarketDataService {

    private final ApplicationEventPublisher publisher;

    public CommonMarketDataService(
            ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void publishMarketData(
            MarketDepthResponse response) {

        publisher.publishEvent(
                new MarketDataEvent(response)
        );
    }
}