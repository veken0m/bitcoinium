
package com.veken0m.mining.btcguild;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Worker {
    
    private String worker_name;
    private float hash_rate;
    private float valid_shares;
    private float stale_shares;
    private float dupe_shares;
    private float unknown_shares;
    private float valid_shares_since_reset;
    private float stale_shares_since_reset;
    private float dupe_shares_since_reset;
    private float unknown_shares_since_reset;
    private float valid_shares_nmc;
    private float stale_shares_nmc;
    private float dupe_shares_nmc;
    private float unknown_shares_nmc;
    private float valid_shares_nmc_since_reset;
    private float stale_shares_nmc_since_reset;
    private float dupe_shares_nmc_since_reset;
    private float unknown_shares_nmc_since_reset;
    private float last_share;
    
    public Worker(@JsonProperty("worker_name") String worker_name, 
            @JsonProperty("hash_rate") float hash_rate, 
            @JsonProperty("valid_shares") float valid_shares, 
            @JsonProperty("stale_shares") float stale_shares,
            @JsonProperty("dupe_shares") float dupe_shares, 
            @JsonProperty("unknown_shares") float unknown_shares, 
            @JsonProperty("valid_shares_since_reset") float valid_shares_since_reset,
            @JsonProperty("stale_shares_since_reset") float stale_shares_since_reset, 
            @JsonProperty("dupe_shares_since_reset") float dupe_shares_since_reset,
            @JsonProperty("unknown_shares_since_reset") float unknown_shares_since_reset, 
            @JsonProperty("valid_shares_nmc") float valid_shares_nmc, 
            @JsonProperty("stale_shares_nmc") float stale_shares_nmc,
            @JsonProperty("dupe_shares_nmc") float dupe_shares_nmc, 
            @JsonProperty("unknown_shares_nmc") float unknown_shares_nmc, 
            @JsonProperty("valid_shares_nmc_since_reset") float valid_shares_nmc_since_reset,
            @JsonProperty("stale_shares_nmc_since_reset") float stale_shares_nmc_since_reset, 
            @JsonProperty("dupe_shares_nmc_since_reset") float dupe_shares_nmc_since_reset,
            @JsonProperty("unknown_shares_nmc_since_reset") float unknown_shares_nmc_since_reset, 
            @JsonProperty("last_share") float last_share) {

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

    public float getHash_rate() {
        return hash_rate;
    }

    public float getValid_shares() {
        return valid_shares;
    }

    public float getStale_shares() {
        return stale_shares;
    }

    public float getDupe_shares() {
        return dupe_shares;
    }

    public float getUnknown_shares() {
        return unknown_shares;
    }

    public float getValid_shares_since_reset() {
        return valid_shares_since_reset;
    }

    public float getStale_shares_since_reset() {
        return stale_shares_since_reset;
    }

    public float getDupe_shares_since_reset() {
        return dupe_shares_since_reset;
    }

    public float getUnknown_shares_since_reset() {
        return unknown_shares_since_reset;
    }

    public float getValid_shares_nmc() {
        return valid_shares_nmc;
    }

    public float getStale_shares_nmc() {
        return stale_shares_nmc;
    }

    public float getDupe_shares_nmc() {
        return dupe_shares_nmc;
    }

    public float getUnknown_shares_nmc() {
        return unknown_shares_nmc;
    }

    public float getValid_shares_nmc_since_reset() {
        return valid_shares_nmc_since_reset;
    }

    public float getStale_shares_nmc_since_reset() {
        return stale_shares_nmc_since_reset;
    }

    public float getDupe_shares_nmc_since_reset() {
        return dupe_shares_nmc_since_reset;
    }

    public float getUnknown_shares_nmc_since_reset() {
        return unknown_shares_nmc_since_reset;
    }

    public float getLast_share() {
        return last_share;
    }

}
