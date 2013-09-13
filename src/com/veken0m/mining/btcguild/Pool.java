
package com.veken0m.mining.btcguild;

import com.fasterxml.jackson.annotation.JsonProperty;


public class Pool {
    
    private Number pool_speed;
    private Number pps_rate;
    private Number difficulty;
    private Number pps_rate_nmc;
    private Number difficulty_nmc;

    public Pool(@JsonProperty("pool_speed") Number pool_speed, 
            @JsonProperty("pps_rate") Number pps_rate, 
            @JsonProperty("difficulty")  Number difficulty, 
            @JsonProperty("pps_rate_nmc")  Number pps_rate_nmc,
            @JsonProperty("difficulty_nmc")  Number difficulty_nmc) {

        this.pool_speed = pool_speed;
        this.pps_rate = pps_rate;
        this.difficulty = difficulty;
        this.pps_rate_nmc = pps_rate_nmc;
        this.difficulty_nmc = difficulty_nmc;
    }

    public Number getPool_speed() {
        return pool_speed;
    }

    public Number getPps_rate() {
        return pps_rate;
    }

    public Number getDifficulty() {
        return difficulty;
    }

    public Number getPps_rate_nmc() {
        return pps_rate_nmc;
    }

    public Number getDifficulty_nmc() {
        return difficulty_nmc;
    }
}
