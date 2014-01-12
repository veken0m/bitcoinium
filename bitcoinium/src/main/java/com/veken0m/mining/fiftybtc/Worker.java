
package com.veken0m.mining.fiftybtc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


public class Worker {
    private final boolean alive;
    private final float blocks_found;
    @JsonIgnore
    private float checkpoint_invalid;
    @JsonIgnore
    private float checkpoint_shares;
    @JsonIgnore
    private float checkpoint_stales;
    private final String hash_rate;
    private float invalid;
    private final long last_share;
    private final float shares;
    private final float stales;
    private final float total_invalid;
    private final float total_shares;
    private final float total_stales;
    private final String worker_name;

    public Worker(@JsonProperty("alive")
                  Boolean alive,
                  @JsonProperty("blocks_found")
                  float blocks_found,
                  @JsonProperty("hash_rate")
                  String hash_rate,
                  @JsonProperty("invalid")
                  float invalid,
                  @JsonProperty("last_share")
                  long last_share,
                  @JsonProperty("shares")
                  float shares,
                  @JsonProperty("stales")
                  float stales,
                  @JsonProperty("total_invalid")
                  float total_invalid,
                  @JsonProperty("total_shares")
                  float total_shares,
                  @JsonProperty("total_stales")
                  float total_stales,
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

    public float getBlocks_found() {
        return this.blocks_found;
    }

    public float getCheckpoint_invalid() {
        return this.checkpoint_invalid;
    }

    public float getCheckpoint_shares() {
        return this.checkpoint_shares;
    }

    public float getCheckpoint_stales() {
        return this.checkpoint_stales;
    }

    public String getHash_rate() {
        return this.hash_rate;
    }

    public float getInvalid() {
        return this.invalid;
    }

    public long getLast_share() {
        return this.last_share;
    }

    public float getShares() {
        return this.shares;
    }

    public float getStales() {
        return this.stales;
    }

    public float getTotal_invalid() {
        return this.total_invalid;
    }

    public float getTotal_shares() {
        return this.total_shares;
    }

    public float getTotal_stales() {
        return this.total_stales;
    }

    public String getWorker_name() {
        return this.worker_name;
    }
}
