package com.s1.kriptoboot.boot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CryptoInfo {

    @JsonProperty("symbol")
    private String symbol; // Kripto para birimi (ör. BTC-USD)

    @JsonProperty("price")
    private String price; // Kripto paranın anlık fiyatı

    @JsonProperty("high24h")
    private String high24h; // Son 24 saatteki en yüksek fiyat

    @JsonProperty("low24h")
    private String low24h; // Son 24 saatteki en düşük fiyat

    private double ema; // EMA (Exponential Moving Average)
    private double rsi; // RSI (Relative Strength Index)
    private String signal; // Alım/Satım sinyali (BUY/SELL/HOLD)

    @JsonProperty("priceHistory")
    private List<Double> priceHistory; // Fiyat geçmişi

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getHigh24h() {
        return high24h;
    }

    public void setHigh24h(String high24h) {
        this.high24h = high24h;
    }

    public String getLow24h() {
        return low24h;
    }

    public void setLow24h(String low24h) {
        this.low24h = low24h;
    }

    public double getEma() {
        return ema;
    }

    public void setEma(double ema) {
        this.ema = ema;
    }

    public double getRsi() {
        return rsi;
    }

    public void setRsi(double rsi) {
        this.rsi = rsi;
    }

    public String getSignal() {
        return signal;
    }

    public void setSignal(String signal) {
        this.signal = signal;
    }

    public List<Double> getPriceHistory() {
        return priceHistory;
    }

    public void setPriceHistory(List<Double> priceHistory) {
        this.priceHistory = priceHistory;
    }
}
