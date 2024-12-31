package com.s1.kriptoboot.boot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SpotBalance {

    @JsonProperty("ccy")
    private String currency; // Para birimi (ör. USD, BTC, vs.)

    @JsonProperty("spotBal")
    private String spotBalance; // Bu para biriminin spot bakiyesi

    // Getter ve Setter metodları
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getSpotBalance() {
        return spotBalance;
    }

    public void setSpotBalance(String spotBalance) {
        this.spotBalance = spotBalance;
    }

    @Override
    public String toString() {
        return "SpotBalance{" +
                "currency='" + currency + '\'' +
                ", spotBalance='" + spotBalance + '\'' +
                '}';
    }
}
