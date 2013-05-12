
package com.veken0m.bitcoinium.mining.bitminter;

import java.util.List;

public class BitMinterData {
    private Number active_workers;
    private Balances balances;
    private Number hash_rate;
    private String name;
    private Number now;
    private Round_start round_start;
    private Shift shift;
    private List<Workers> workers;

    public Number getActive_workers() {
        return this.active_workers;
    }

    public void setActive_workers(Number active_workers) {
        this.active_workers = active_workers;
    }

    public Balances getBalances() {
        return this.balances;
    }

    public void setBalances(Balances balances) {
        this.balances = balances;
    }

    public Number getHash_rate() {
        return this.hash_rate;
    }

    public void setHash_rate(Number hash_rate) {
        this.hash_rate = hash_rate;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Number getNow() {
        return this.now;
    }

    public void setNow(Number now) {
        this.now = now;
    }

    public Round_start getRound_start() {
        return this.round_start;
    }

    public void setRound_start(Round_start round_start) {
        this.round_start = round_start;
    }

    public Shift getShift() {
        return this.shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    public List<Workers> getWorkers() {
        return this.workers;
    }

    public void setWorkers(List<Workers> workers) {
        this.workers = workers;
    }
}
