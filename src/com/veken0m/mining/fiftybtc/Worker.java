
package com.veken0m.mining.fiftybtc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


public class Worker {
    private boolean alive;
    private Number blocks_found;
    @JsonIgnore
    private Number checkpoint_invalid;
    @JsonIgnore
    private Number checkpoint_shares;
    @JsonIgnore
    private Number checkpoint_stales;
    private String hash_rate;
    private Number invalid;
    private Number last_share;
    private Number shares;
    private Number stales;
    private Number total_invalid;
    private Number total_shares;
    private Number total_stales;
    private String worker_name;

    public Worker(@JsonProperty("alive")
    Boolean alive,
            @JsonProperty("blocks_found")
            Number blocks_found,
            @JsonProperty("hash_rate")
            String hash_rate,
            @JsonProperty("invalid")
            Number invalid,
            @JsonProperty("last_share")
            Number last_share,
            @JsonProperty("shares")
            Number shares,
            @JsonProperty("stales")
            Number stales,
            @JsonProperty("total_invalid")
            Number total_invalid,
            @JsonProperty("total_shares")
            Number total_shares,
            @JsonProperty("total_stales")
            Number total_stales,
            @JsonProperty("worker_name")
            String worker_name) {
        this.alive = alive;
        this.blocks_found = blocks_found;
        this.hash_rate = hash_rate;
        this.invalid = invalid;
        this.last_share = last_share;
        this.shares = shares;
        this.stales = stales;
        this.total_invalid = total_invalid;
        this.total_shares = total_shares;
        this.invalid = invalid;
        this.total_stales = total_stales;
        this.worker_name = worker_name;
    }

    public boolean getAlive() {
        return this.alive;
    }

    public Number getBlocks_found() {
        return this.blocks_found;
    }

    public Number getCheckpoint_invalid() {
        return this.checkpoint_invalid;
    }

    public Number getCheckpoint_shares() {
        return this.checkpoint_shares;
    }

    public Number getCheckpoint_stales() {
        return this.checkpoint_stales;
    }

    public String getHash_rate() {
        return this.hash_rate;
    }

    public Number getInvalid() {
        return this.invalid;
    }

    public Number getLast_share() {
        return this.last_share;
    }

    public Number getShares() {
        return this.shares;
    }

    public Number getStales() {
        return this.stales;
    }

    public Number getTotal_invalid() {
        return this.total_invalid;
    }

    public Number getTotal_shares() {
        return this.total_shares;
    }

    public Number getTotal_stales() {
        return this.total_stales;
    }

    public String getWorker_name() {
        return this.worker_name;
    }
}
