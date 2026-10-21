package com.trading.app.contractmaster.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;

public class InstrumentMasterRequest {

     @JsonProperty("Token")
    @JsonAlias("token")
    private String token;

    @JsonProperty("Exch")
    @JsonAlias("exch")
    private String exchange;

    @JsonProperty("Symbol")
    @JsonAlias("symbol")
    private String symbol;

    @JsonProperty("Instrument Name")
    private String instrumentName;

    @JsonProperty("Instrument Type")
    private String instrumentType;

    private String underlying;

    @JsonProperty("Strike Price")
    private BigDecimal strikePrice;

    @JsonProperty("Option Type")
    private String optionType;

    @JsonProperty("Expiry Date")
    private LocalDate expiryDate;

    @JsonProperty("Lot Size")
    private Integer lotSize;

    @JsonProperty("Trading Symbol")
    private String tradingSymbol;

    @JsonProperty("Formatted Ins Name")
    private String formattedInstrumentName;

    @JsonProperty("Exchange Segment")
    private String exchangeSegment;

    @JsonProperty("Tick Size")
    private BigDecimal tickSize;

    private String sourceFile;

    private Boolean active = true;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getInstrumentName() {
        return instrumentName;
    }

    public void setInstrumentName(String instrumentName) {
        this.instrumentName = instrumentName;
    }

    public String getInstrumentType() {
        return instrumentType;
    }

    public void setInstrumentType(String instrumentType) {
        this.instrumentType = instrumentType;
    }

    public String getUnderlying() {
        return underlying;
    }

    public void setUnderlying(String underlying) {
        this.underlying = underlying;
    }

    public BigDecimal getStrikePrice() {
        return strikePrice;
    }

    public void setStrikePrice(BigDecimal strikePrice) {
        this.strikePrice = strikePrice;
    }

    public String getOptionType() {
        return optionType;
    }

    public void setOptionType(String optionType) {
        this.optionType = optionType;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Integer getLotSize() {
        return lotSize;
    }

    public void setLotSize(Integer lotSize) {
        this.lotSize = lotSize;
    }

    public String getTradingSymbol() {
        return tradingSymbol;
    }

    public void setTradingSymbol(String tradingSymbol) {
        this.tradingSymbol = tradingSymbol;
    }

    public String getFormattedInstrumentName() {
        return formattedInstrumentName;
    }

    public void setFormattedInstrumentName(String formattedInstrumentName) {
        this.formattedInstrumentName = formattedInstrumentName;
    }

    public String getExchangeSegment() {
        return exchangeSegment;
    }

    public void setExchangeSegment(String exchangeSegment) {
        this.exchangeSegment = exchangeSegment;
    }

    public BigDecimal getTickSize() {
        return tickSize;
    }

    public void setTickSize(BigDecimal tickSize) {
        this.tickSize = tickSize;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}