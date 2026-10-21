package com.trading.app.optionchain.service;

import com.trading.app.datapublisher.MarketDataEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class OptionsGammaScanner {


    @EventListener
    void getOptionsGammaDetails(MarketDataEvent event){

    }
}
