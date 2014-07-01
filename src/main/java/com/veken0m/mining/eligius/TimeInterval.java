package com.veken0m.mining.eligius;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TimeInterval {

    private final float hashrate;
    private final float interval;
    private final String interval_name;
    private final float shares;

    public TimeInterval(
            @JsonProperty("hashrate")
            float hashrate,
            @JsonProperty("interval")
            float interval,
            @JsonProperty("interval_name")
            String interval_name,
            @JsonProperty("shares")
            float shares) {

        this.hashrate = hashrate;
        this.interval = interval;
        this.interval_name = interval_name;
        this.shares = shares;
    }

    public float getHashrate() {
        return this.hashrate;
    }

    public float getInterval() {
        return this.interval;
    }

    public String getInterval_name() {
        return this.interval_name;
    }

    public float getShares() {
        return this.shares;
    }
}
