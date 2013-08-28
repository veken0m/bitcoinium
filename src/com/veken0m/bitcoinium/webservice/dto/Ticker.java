
package com.veken0m.bitcoinium.webservice.dto;

import java.math.BigDecimal;

public class Ticker {
    private BigDecimal last;
    private BigDecimal timestamp;
    private BigDecimal volume;

    public BigDecimal getLast() {
        return this.last;
    }

    public void setLast(BigDecimal last) {
        this.last = last;
    }

    public BigDecimal getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(BigDecimal timestamp) {
        this.timestamp = timestamp;
    }

    public BigDecimal getVolume() {
        return this.volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }
}
