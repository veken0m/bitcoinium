
package com.veken0m.bitcoinium.webservice.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Ticker {
    private final BigDecimal last;
    private final BigDecimal timestamp;
    private final BigDecimal volume;

    public Ticker(
            @JsonProperty("last") BigDecimal last, 
            @JsonProperty("timestamp") BigDecimal timestamp, 
            @JsonProperty("volume") BigDecimal volume) {
        
        this.last = last;
        this.timestamp = timestamp;
        this.volume = volume;
    }

    public BigDecimal getLast() {
        return this.last;
    }

    public BigDecimal getTimestamp() {
        return this.timestamp;
    }

    public BigDecimal getVolume() {
        return this.volume;
    }
}
