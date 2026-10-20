package com.trading.app.futureselector;

import com.trading.app.datapublisher.MarketDataEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class NiftyFuture {

    @EventListener
    public void onMarketData(MarketDataEvent event) {

        var data = event.response();

        if (!"NFO".equals(data.getExchange())) {
            return;
        }

//        if (!futuresToken.equals(data.getToken())) {
//            return;
//        }

        if (data.getLastTradedPrice() == null) {
            return;
        }

        try {
            BigDecimal price = new BigDecimal(
                    data.getLastTradedPrice()
            );

//            if (price.signum() > 0) {
//                latestPrice = price;
//            }

        } catch (NumberFormatException ignored) {
            // Ignore invalid price updates.
        }
    }

//    public BigDecimal getLatestPrice() {
//        return latestPrice;
//    }
}
