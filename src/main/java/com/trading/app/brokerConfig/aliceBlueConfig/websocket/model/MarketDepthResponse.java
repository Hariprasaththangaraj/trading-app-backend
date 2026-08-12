package com.trading.app.brokerConfig.aliceBlueConfig.websocket.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MarketDepthResponse {

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

    @JsonProperty("ltq")
    private String lastTradedQuantity;

    @JsonProperty("ltt")
    private String lastTradedTime;

    @JsonProperty("ft")
    private String feedTime;

    @JsonProperty("tbq")
    private String totalBuyQuantity;

    @JsonProperty("tsq")
    private String totalSellQuantity;

    @JsonProperty("uc")
    private String upperCircuit;

    @JsonProperty("lc")
    private String lowerCircuit;

    @JsonProperty("bp1")
    private String buyPrice1;

    @JsonProperty("bp2")
    private String buyPrice2;

    @JsonProperty("bp3")
    private String buyPrice3;

    @JsonProperty("bp4")
    private String buyPrice4;

    @JsonProperty("bp5")
    private String buyPrice5;

    @JsonProperty("sp1")
    private String sellPrice1;

    @JsonProperty("sp2")
    private String sellPrice2;

    @JsonProperty("sp3")
    private String sellPrice3;

    @JsonProperty("sp4")
    private String sellPrice4;

    @JsonProperty("sp5")
    private String sellPrice5;

    @JsonProperty("bq1")
    private String buyQuantity1;

    @JsonProperty("bq2")
    private String buyQuantity2;

    @JsonProperty("bq3")
    private String buyQuantity3;

    @JsonProperty("bq4")
    private String buyQuantity4;

    @JsonProperty("bq5")
    private String buyQuantity5;

    @JsonProperty("sq1")
    private String sellQuantity1;

    @JsonProperty("sq2")
    private String sellQuantity2;

    @JsonProperty("sq3")
    private String sellQuantity3;

    @JsonProperty("sq4")
    private String sellQuantity4;

    @JsonProperty("sq5")
    private String sellQuantity5;

    @JsonProperty("bo1")
    private String buyOrders1;

    @JsonProperty("bo2")
    private String buyOrders2;

    @JsonProperty("bo3")
    private String buyOrders3;

    @JsonProperty("bo4")
    private String buyOrders4;

    @JsonProperty("bo5")
    private String buyOrders5;

    @JsonProperty("so1")
    private String sellOrders1;

    @JsonProperty("so2")
    private String sellOrders2;

    @JsonProperty("so3")
    private String sellOrders3;

    @JsonProperty("so4")
    private String sellOrders4;

    @JsonProperty("so5")
    private String sellOrders5;

    @JsonProperty("52h")
    private String week52High;

    @JsonProperty("52l")
    private String week52Low;
}