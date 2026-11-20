package com.trading.app.niftyalgo.service.impl;

import com.trading.app.brokerConfig.websocket.model.MarketDepthResponse;
import com.trading.app.datapublisher.MarketDataEvent;
import com.trading.app.niftyalgo.service.AlgoService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@Service
public class AlgoServiceImpl implements AlgoService {
    // Strategy Configuration
    private final LocalTime VWAP_START = LocalTime.of(9, 15);
    private final LocalTime STRATEGY_START = LocalTime.of(9, 30);
    private final LocalTime STRATEGY_END = LocalTime.of(15, 15);
    private final ZoneId zone = ZoneId.of("Asia/Kolkata");
    // VWAP Variables
    private BigDecimal cumulativePV = BigDecimal.ZERO;
    private BigDecimal cumulativeVolume = BigDecimal.ZERO;
    private BigDecimal previousVolume = BigDecimal.ZERO;
    private BigDecimal vwap = BigDecimal.ZERO;
    private boolean volumeInitialized = false;
    // Live Tick Variables
    private BigDecimal tickLtp;
    private LocalDateTime tickTime;
    // Current 5-Minute Candle
    private BigDecimal candleOpen;
    private BigDecimal candleClose;
    private BigDecimal candleVwap;
    private LocalDateTime candleStart;
    // Completed Candle
    private BigDecimal completedOpen;
    private BigDecimal completedClose;
    private BigDecimal completedVwap;
    private LocalDateTime completedCandleStart;
    private LocalDate tradingDate;
    private volatile String latestSignal = "WAITING";
    // ==========================================
    // MAIN METHOD - EVENT LISTENER
    // ==========================================

    @EventListener
    public synchronized void handleMarketData(MarketDataEvent event) {
        // 1. Check VWAP Value
        checkVwapValue(event);
        // 2. Check 5 Minutes Time Frame
        boolean candleCompleted = checkFiveMinuteTimeFrame();
        // 3. Check Signal
        if (candleCompleted) {
            latestSignal = checkSignal();
        }
        //4. Check and Select Options CE / PE By using Option Chain API
        //5. Need to check Order flow
        //6. Execute Order with Stoploss / Trailing Stoploss
        //7. Store the data in database with P/L Report

    }
    // ==========================================
    // 1. CHECK VWAP VALUE
    // ==========================================

    private void checkVwapValue(MarketDataEvent event) {
        tickLtp = null;
        MarketDepthResponse response = event.response();
        if (response==null) return;
        tickLtp = new BigDecimal(response.getLastTradedPrice());
        BigDecimal volume = new BigDecimal(response.getVolume());
        tickTime = LocalDateTime.now(zone);
        resetDailyData(tickTime.toLocalDate());
        LocalTime time = tickTime.toLocalTime();
        if (time.isBefore(VWAP_START) || !time.isBefore(STRATEGY_END)) {
            return;
        }
        // Initialize volume baseline
        if (!volumeInitialized) {
            previousVolume = volume;
            volumeInitialized = true;
            return;
        }
        BigDecimal tradedVolume = volume.subtract(previousVolume);
        if (tradedVolume.compareTo(BigDecimal.ZERO) > 0) {
            cumulativePV = cumulativePV.add(tickLtp.multiply(tradedVolume));
            cumulativeVolume = cumulativeVolume.add(tradedVolume);
            previousVolume = volume;
            vwap = cumulativePV.divide(cumulativeVolume, 2, RoundingMode.HALF_UP);
        }
    }
    // ==========================================
    // 2. CHECK FIVE MINUTE TIME FRAME
    // ==========================================

    private boolean checkFiveMinuteTimeFrame() {
        if (tickLtp==null || tickTime==null) {
            return false;
        }
        if (tickTime.toLocalTime().isBefore(VWAP_START)) {
            return false;
        }
        LocalDateTime currentCandleStart = tickTime.truncatedTo(ChronoUnit.MINUTES).withMinute((tickTime.getMinute() / 5) * 5);
        // First candle
        if (candleStart==null) {
            if (tickTime.toLocalTime().isBefore(STRATEGY_END)) {
                startNewCandle(currentCandleStart);
            }
            return false;
        }
        // Same candle - update close
        if (candleStart.equals(currentCandleStart)) {
            candleClose = tickLtp;
            candleVwap = vwap;
            return false;
        }
        // New candle - previous candle completed
        if (currentCandleStart.isAfter(candleStart)) {
            completedOpen = candleOpen;
            completedClose = candleClose;
            completedVwap = candleVwap;
            completedCandleStart = candleStart;
            // Start next candle if within trading time
            if (tickTime.toLocalTime().isBefore(STRATEGY_END)) {
                startNewCandle(currentCandleStart);
            } else {
                candleStart = null;
            }
            return true;
        }
        return false;
    }
    // ==========================================
    // START NEW CANDLE
    // ==========================================

    private void startNewCandle(LocalDateTime time) {
        candleStart = time;
        candleOpen = tickLtp;
        candleClose = tickLtp;
        candleVwap = vwap;
    }
    // ==========================================
    // 3. CHECK SIGNAL
    // ==========================================

    private String checkSignal() {
        if (completedOpen==null || completedClose==null || completedVwap==null || completedVwap.compareTo(BigDecimal.ZERO) <= 0) {
            return "NO SIGNAL";
        }
        LocalDateTime candleEnd = completedCandleStart.plusMinutes(5);
        LocalTime candleStartTime = completedCandleStart.toLocalTime();
        LocalTime candleEndTime = candleEnd.toLocalTime();
        // Trading time validation
        if (candleStartTime.isBefore(STRATEGY_START) || candleEndTime.isAfter(STRATEGY_END)) {
            return "NO SIGNAL";
        }
        // BULLISH
        if (completedOpen.compareTo(completedVwap) < 0 && completedClose.compareTo(completedVwap) > 0) {
            printSignal("BULLISH");
            return "BULLISH";
        }
        // BEARISH
        if (completedOpen.compareTo(completedVwap) > 0 && completedClose.compareTo(completedVwap) < 0) {
            printSignal("BEARISH");
            return "BEARISH";
        }
        printSignal("NO SIGNAL");
        return "NO SIGNAL";
    }
    // ==========================================
    // PRINT SIGNAL
    // ==========================================

    private void printSignal(String signal) {
        System.out.println("===== NIFTY FUTURES VWAP =====");
        System.out.println("Candle : " + completedCandleStart);
        System.out.println("Open   : " + completedOpen);
        System.out.println("Close  : " + completedClose);
        System.out.println("VWAP   : " + completedVwap);
        System.out.println("Signal : " + signal);
        System.out.println("==============================");
    }
    // ==========================================
    // DAILY RESET
    // ==========================================

    private void resetDailyData(LocalDate date) {
        if (tradingDate==null || !tradingDate.equals(date)) {
            tradingDate = date;
            cumulativePV = BigDecimal.ZERO;
            cumulativeVolume = BigDecimal.ZERO;
            previousVolume = BigDecimal.ZERO;
            vwap = BigDecimal.ZERO;
            volumeInitialized = false;
            candleStart = null;
            candleOpen = null;
            candleClose = null;
            candleVwap = null;
            completedOpen = null;
            completedClose = null;
            completedVwap = null;
            completedCandleStart = null;
            latestSignal = "WAITING";
        }
    }
    // ==========================================
    // GET LATEST SIGNAL
    // ==========================================

    public String getLatestSignal() {
        return latestSignal;
    }
}
