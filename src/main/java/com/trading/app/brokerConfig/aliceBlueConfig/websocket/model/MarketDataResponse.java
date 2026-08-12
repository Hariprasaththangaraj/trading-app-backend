package com.trading.app.brokerConfig.aliceBlueConfig.websocket.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MarketDataResponse {

    @JsonProperty("t")
    private String type;

    @JsonProperty("e")
    private String exchange;

    @JsonProperty("tk")
    private String token;

    @JsonProperty("ts")
    private String symbol;

    @JsonProperty("lp")
    private String lastTradedPrice;

    @JsonProperty("pc")
    private String percentageChange;

    @JsonProperty("cv")
    private String changeValue;

    @JsonProperty("v")
    private String volume;

    @JsonProperty("o")
    private String open;

    @JsonProperty("h")
    private String high;

    @JsonProperty("l")
    private String low;

    @JsonProperty("c")
    private String close;

    @JsonProperty("ap")
    private String averagePrice;

    @JsonProperty("oi")
    private String openInterest;

    @JsonProperty("ft")
    private String feedTime;

    @JsonProperty("bp1")
    private String buyPrice1;

    @JsonProperty("sp1")
    private String sellPrice1;

    @JsonProperty("bq1")
    private String buyQuantity1;

    @JsonProperty("sq1")
    private String sellQuantity1;

    @JsonProperty("pp")
    private String precision;

    @JsonProperty("ml")
    private String multiplier;

    @JsonProperty("ls")
    private String lotSize;

    @JsonProperty("ti")
    private String tickSize;
}