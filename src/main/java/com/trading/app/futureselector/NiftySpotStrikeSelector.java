package com.trading.app.futureselector;

import com.trading.app.datapublisher.MarketDataEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class NiftySpotStrikeSelector  {



    @EventListener
    public void getCurrentSpotPrice(MarketDataEvent event) {




    }

}
