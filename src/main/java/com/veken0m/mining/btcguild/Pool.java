package com.veken0m.mining.btcguild;

import com.fasterxml.jackson.annotation.JsonProperty;


public class Pool {

    private final float pool_speed;
    private final float pps_rate;
    private final float difficulty;
    private final float pps_rate_nmc;
    private final float difficulty_nmc;

    public Pool(@JsonProperty("pool_speed") float pool_speed,
                @JsonProperty("pps_rate") float pps_rate,
                @JsonProperty("difficulty") float difficulty,
                @JsonProperty("pps_rate_nmc") float pps_rate_nmc,
                @JsonProperty("difficulty_nmc") float difficulty_nmc) {

        this.pool_speed = pool_speed;
        this.pps_rate = pps_rate;
        this.difficulty = difficulty;
        this.pps_rate_nmc = pps_rate_nmc;
        this.difficulty_nmc = difficulty_nmc;
    }

    public float getPool_speed() {
        return pool_speed;
    }

    public float getPps_rate() {
        return pps_rate;
    }

    public float getDifficulty() {
        return difficulty;
    }

    public float getPps_rate_nmc() {
        return pps_rate_nmc;
    }

    public float getDifficulty_nmc() {
        return difficulty_nmc;
    }
}
