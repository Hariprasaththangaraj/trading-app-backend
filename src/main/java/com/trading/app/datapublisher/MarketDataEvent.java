package com.trading.app.datapublisher;

import com.trading.app.brokerConfig.websocket.model.MarketDepthResponse;

public record MarketDataEvent(MarketDepthResponse response) {
}
