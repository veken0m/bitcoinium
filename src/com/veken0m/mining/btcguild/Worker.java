
package com.veken0m.mining.btcguild;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Worker {
    
    private String worker_name;
    private Number hash_rate;
    private Number valid_shares;
    private Number stale_shares;
    private Number dupe_shares;
    private Number unknown_shares;
    private Number valid_shares_since_reset;
    private Number stale_shares_since_reset;
    private Number dupe_shares_since_reset;
    private Number unknown_shares_since_reset;
    private Number valid_shares_nmc;
    private Number stale_shares_nmc;
    private Number dupe_shares_nmc;
    private Number unknown_shares_nmc;
    private Number valid_shares_nmc_since_reset;
    private Number stale_shares_nmc_since_reset;
    private Number dupe_shares_nmc_since_reset;
    private Number unknown_shares_nmc_since_reset;
    private Number last_share;
    
    public Worker(@JsonProperty("worker_name") String worker_name, 
            @JsonProperty("hash_rate") Number hash_rate, 
            @JsonProperty("valid_shares") Number valid_shares, 
            @JsonProperty("stale_shares") Number stale_shares,
            @JsonProperty("dupe_shares") Number dupe_shares, 
            @JsonProperty("unknown_shares") Number unknown_shares, 
            @JsonProperty("valid_shares_since_reset") Number valid_shares_since_reset,
            @JsonProperty("stale_shares_since_reset") Number stale_shares_since_reset, 
            @JsonProperty("dupe_shares_since_reset") Number dupe_shares_since_reset,
            @JsonProperty("unknown_shares_since_reset") Number unknown_shares_since_reset, 
            @JsonProperty("valid_shares_nmc") Number valid_shares_nmc, 
            @JsonProperty("stale_shares_nmc") Number stale_shares_nmc,
            @JsonProperty("dupe_shares_nmc") Number dupe_shares_nmc, 
            @JsonProperty("unknown_shares_nmc") Number unknown_shares_nmc, 
            @JsonProperty("valid_shares_nmc_since_reset") Number valid_shares_nmc_since_reset,
            @JsonProperty("stale_shares_nmc_since_reset") Number stale_shares_nmc_since_reset, 
            @JsonProperty("dupe_shares_nmc_since_reset") Number dupe_shares_nmc_since_reset,
            @JsonProperty("unknown_shares_nmc_since_reset") Number unknown_shares_nmc_since_reset, 
            @JsonProperty("last_share") Number last_share) {

        this.worker_name = worker_name;
        this.hash_rate = hash_rate;
        this.valid_shares = valid_shares;
        this.stale_shares = stale_shares;
        this.dupe_shares = dupe_shares;
        this.unknown_shares = unknown_shares;
        this.valid_shares_since_reset = valid_shares_since_reset;
        this.stale_shares_since_reset = stale_shares_since_reset;
        this.dupe_shares_since_reset = dupe_shares_since_reset;
        this.unknown_shares_since_reset = unknown_shares_since_reset;
        this.valid_shares_nmc = valid_shares_nmc;
        this.stale_shares_nmc = stale_shares_nmc;
        this.dupe_shares_nmc = dupe_shares_nmc;
        this.unknown_shares_nmc = unknown_shares_nmc;
        this.valid_shares_nmc_since_reset = valid_shares_nmc_since_reset;
        this.stale_shares_nmc_since_reset = stale_shares_nmc_since_reset;
        this.dupe_shares_nmc_since_reset = dupe_shares_nmc_since_reset;
        this.unknown_shares_nmc_since_reset = unknown_shares_nmc_since_reset;
        this.last_share = last_share;
    }

    public String getWorker_name() {
        return worker_name;
    }

    public Number getHash_rate() {
        return hash_rate;
    }

    public Number getValid_shares() {
        return valid_shares;
    }

    public Number getStale_shares() {
        return stale_shares;
    }

    public Number getDupe_shares() {
        return dupe_shares;
    }

    public Number getUnknown_shares() {
        return unknown_shares;
    }

    public Number getValid_shares_since_reset() {
        return valid_shares_since_reset;
    }

    public Number getStale_shares_since_reset() {
        return stale_shares_since_reset;
    }

    public Number getDupe_shares_since_reset() {
        return dupe_shares_since_reset;
    }

    public Number getUnknown_shares_since_reset() {
        return unknown_shares_since_reset;
    }

    public Number getValid_shares_nmc() {
        return valid_shares_nmc;
    }

    public Number getStale_shares_nmc() {
        return stale_shares_nmc;
    }

    public Number getDupe_shares_nmc() {
        return dupe_shares_nmc;
    }

    public Number getUnknown_shares_nmc() {
        return unknown_shares_nmc;
    }

    public Number getValid_shares_nmc_since_reset() {
        return valid_shares_nmc_since_reset;
    }

    public Number getStale_shares_nmc_since_reset() {
        return stale_shares_nmc_since_reset;
    }

    public Number getDupe_shares_nmc_since_reset() {
        return dupe_shares_nmc_since_reset;
    }

    public Number getUnknown_shares_nmc_since_reset() {
        return unknown_shares_nmc_since_reset;
    }

    public Number getLast_share() {
        return last_share;
    }

}
