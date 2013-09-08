
package com.veken0m.bitcoinium.webservice.dto;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Orderbook {
    private final long ageInMs;
    private final ArrayList<BigDecimal> ap;
    private final ArrayList<BigDecimal> av;
    private final ArrayList<BigDecimal> bp;
    private final ArrayList<BigDecimal> bv;
    private final BigDecimal last;
    private final long timestamp;

    public Orderbook(@JsonProperty("ageInMs")long ageInMs, 
            @JsonProperty("ap") ArrayList<BigDecimal> ap, 
            @JsonProperty("av") ArrayList<BigDecimal> av,
            @JsonProperty("bp") ArrayList<BigDecimal> bp, 
            @JsonProperty("bv") ArrayList<BigDecimal> bv, 
            @JsonProperty("last") BigDecimal last, 
            @JsonProperty("timestamp") long timestamp) {
        
        this.ageInMs = ageInMs;
        this.ap = ap;
        this.av = av;
        this.bp = bp;
        this.bv = bv;
        this.last = last;
        this.timestamp = timestamp;
    }

    public long getAgeInMs() {
        return this.ageInMs;
    }

    public ArrayList<BigDecimal> getAskPriceList() {
        return this.ap;
    }

    public ArrayList<BigDecimal> getAskVolumeList() {
        return this.av;
    }

    public ArrayList<BigDecimal> getBidPriceList() {
        return this.bp;
    }

    public ArrayList<BigDecimal> getBidVolumeList() {
        return this.bv;
    }

    public BigDecimal getLast() {
        return this.last;
    }

    public long getTimestamp() {
        return this.timestamp;
    }
}
