package com.s1.kriptoboot.boot.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TotalBalance {

    @JsonProperty("totalEq")
    private String totalEq; // Cüzdandaki tüm paranın toplam değeri

    // Getter ve Setter metodları
    public String getTotalEq() {
        return totalEq;
    }

    public void setTotalEq(String totalEq) {
        this.totalEq = totalEq;
    }

    @Override
    public String toString() {
        return "TotalBalance{" +
                "totalEq='" + totalEq + '\'' +
                '}';
    }
}
