package com.trading.app.contractmaster.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "instrument_master",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_exchange_token",
                        columnNames = {"exchange", "token"}
                )
        },
        indexes = {
                @Index(name = "idx_underlying_expiry", columnList = "underlying, expiry_date"),
                @Index(name = "idx_instrument_type", columnList = "instrument_type")
        }
)
public class InstrumentMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String token;

    @Column(nullable = false, length = 100)
    private String symbol;

    @Column(name = "exchange", nullable = false, length = 20)
    private String exchange;

    @Column(name = "instrument_name", length = 100)
    private String instrumentName;

    @Column(name = "instrument_type", nullable = false, length = 30)
    private String instrumentType;

    @Column(length = 100)
    private String underlying;

    @Column(name = "strike_price", precision = 12, scale = 2)
    private BigDecimal strikePrice;

    @Column(name = "option_type", length = 5)
    private String optionType;

    private LocalDate expiryDate;

    private Integer lotSize;

    @Column(length = 100)
    private String tradingSymbol;

    @Column(name = "formatted_instrument_name", length = 150)
    private String formattedInstrumentName;

    @Column(name = "exchange_segment", length = 30)
    private String exchangeSegment;

    @Column(name = "tick_size", precision = 10, scale = 4)
    private BigDecimal tickSize;

    @Column(name = "source_file", length = 255)
    private String sourceFile;

    @Column(name = "imported_at", insertable = false, updatable = false)
    private LocalDateTime importedAt;

    @Column(nullable = false)
    private Boolean active = true;

    public InstrumentMaster() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public LocalDateTime getImportedAt() {
        return importedAt;
    }

    public void setImportedAt(LocalDateTime importedAt) {
        this.importedAt = importedAt;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}