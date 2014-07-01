package com.veken0m.mining.fiftybtc;

import com.fasterxml.jackson.annotation.JsonProperty;


public class Worker {
    private final long last_share;
    private final float total_shares;
    private final String worker_name;

    public Worker(@JsonProperty("last_share") long last_share,
                  @JsonProperty("total_shares") float total_shares,
                  @JsonProperty("worker_name") String worker_name) {

        this.last_share = last_share;
        this.total_shares = total_shares;
        this.worker_name = worker_name;
    }

    public long getLast_share() {
        return this.last_share;
    }

    public float getTotal_shares() {
        return this.total_shares;
    }

    public String getWorker_name() {
        return this.worker_name;
    }
}
