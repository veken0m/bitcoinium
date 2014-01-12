
package com.veken0m.bitcoinium.webservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.ArrayList;

public class TickerHistory {
    private final ArrayList<BigDecimal> pp;
    private final long t;
    private final ArrayList<BigDecimal> tt;

    public TickerHistory(
            @JsonProperty("pp") ArrayList<BigDecimal> pp,
            @JsonProperty("t") long t,
            @JsonProperty("tt") ArrayList<BigDecimal> tt) {

        this.pp = pp;
        this.t = t;
        this.tt = tt;
    }

    public ArrayList<BigDecimal> getPriceHistoryList() {
        return this.pp;
    }

    public long getBaseTimestamp() {
        return this.t;
    }

    public ArrayList<BigDecimal> getTimeStampOffsets() {
        return this.tt;
    }
}
