package com.trading.app.futureselector;

import com.trading.app.datapublisher.MarketDataEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class NiftySpotStrikeSelector  {



    @EventListener
    public void getCurrentSpotPrice(MarketDataEvent event) {




    }

}
