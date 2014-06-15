
package com.veken0m.mining.slush;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Slush {
    private final float confirmed_nmc_reward;
    private final float confirmed_reward;
    private final float estimated_reward;
    private final float hashrate;
    @JsonIgnore
    private float nmc_send_threshold;
    @JsonIgnore
    private String rating;
    @JsonIgnore
    private float send_threshold;
    private float unconfirmed_nmc_reward;
    private final float unconfirmed_reward;
    private final String username;
    @JsonIgnore
    private String wallet;
    private final Workers workers;

    public Slush(
            @JsonProperty("confirmed_nmc_reward")
            float confirmed_nmc_reward,
            @JsonProperty("confirmed_reward")
            float confirmed_reward,
            @JsonProperty("estimated_reward")
            float estimated_reward,
            @JsonProperty("hashrate")
            float hashrate,
            @JsonProperty("unconfirmed_nmc_reward")
            float unconfirmed_nmc_reward,
            @JsonProperty("unconfirmed_reward")
            float unconfirmed_reward,
            @JsonProperty("username")
            String username,
            @JsonProperty("workers")
            Workers workers) {
        this.confirmed_nmc_reward = confirmed_nmc_reward;
        this.confirmed_reward = confirmed_reward;
        this.estimated_reward = estimated_reward;
        this.hashrate = hashrate;
        this.unconfirmed_nmc_reward = unconfirmed_nmc_reward;
        this.unconfirmed_reward = unconfirmed_reward;
        this.username = username;
        this.workers = workers;
    }

    public float getConfirmed_nmc_reward() {
        return this.confirmed_nmc_reward;
    }

    public float getConfirmed_reward() {
        return this.confirmed_reward;
    }

    public float getEstimated_reward() {
        return this.estimated_reward;
    }

    public float getHashrate() {
        return this.hashrate;
    }

    public float getNmc_send_threshold() {
        return this.nmc_send_threshold;
    }

    public String getRating() {
        return this.rating;
    }

    public float getSend_threshold() {
        return this.send_threshold;
    }

    public float getUnconfirmed_nmc_reward() {
        return this.unconfirmed_nmc_reward;
    }

    public void setUnconfirmed_nmc_reward(float unconfirmed_nmc_reward) {
        this.unconfirmed_nmc_reward = unconfirmed_nmc_reward;
    }

    public float getUnconfirmed_reward() {
        return this.unconfirmed_reward;
    }

    public String getUsername() {
        return this.username;
    }

    public String getWallet() {
        return this.wallet;
    }

    public Workers getWorkers() {
        return this.workers;
    }
}
