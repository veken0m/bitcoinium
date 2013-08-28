
package com.veken0m.bitcoinium.webservice.dto;

import java.math.BigDecimal;
import java.util.ArrayList;

public class TickerHistory {
    private ArrayList<BigDecimal> pp;
    private long t;
    private ArrayList<BigDecimal> tt;

    public ArrayList<BigDecimal> getPp() {
        return this.pp;
    }

    public void setPp(ArrayList<BigDecimal> pp) {
        this.pp = pp;
    }

    public long getT() {
        return this.t;
    }

    public void setT(long t) {
        this.t = t;
    }

    public ArrayList<BigDecimal> getTt() {
        return this.tt;
    }

    public void setTt(ArrayList<BigDecimal> tt) {
        this.tt = tt;
    }
}
