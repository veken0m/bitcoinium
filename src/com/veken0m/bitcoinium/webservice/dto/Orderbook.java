
package com.veken0m.bitcoinium.webservice.dto;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Orderbook {
    private long ageInMs;
    private ArrayList<BigDecimal> ap;
    private ArrayList<BigDecimal> av;
    private ArrayList<BigDecimal> bp;
    private ArrayList<BigDecimal> bv;
    private BigDecimal last;
    private long timestamp;

    public long getAgeInMs() {
        return this.ageInMs;
    }

    public void setAgeInMs(long ageInMs) {
        this.ageInMs = ageInMs;
    }

    public ArrayList<BigDecimal> getAp() {
        return this.ap;
    }

    public void setAp(ArrayList<BigDecimal> ap) {
        this.ap = ap;
    }

    public ArrayList<BigDecimal> getAv() {
        return this.av;
    }

    public void setAv(ArrayList<BigDecimal> av) {
        this.av = av;
    }

    public ArrayList<BigDecimal> getBp() {
        return this.bp;
    }

    public void setBp(ArrayList<BigDecimal> bp) {
        this.bp = bp;
    }

    public ArrayList<BigDecimal> getBv() {
        return this.bv;
    }

    public void setBv(ArrayList<BigDecimal> bv) {
        this.bv = bv;
    }

    public BigDecimal getLast() {
        return this.last;
    }

    public void setLast(BigDecimal last) {
        this.last = last;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
