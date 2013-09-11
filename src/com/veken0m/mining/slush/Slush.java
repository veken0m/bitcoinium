
package com.veken0m.mining.slush;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Slush {
    private String confirmed_nmc_reward;
    private String confirmed_reward;
    private String estimated_reward;
    private String hashrate;
    @JsonIgnore
    private String nmc_send_threshold;
    @JsonIgnore
    private String rating;
    @JsonIgnore
    private String send_threshold;
    private String unconfirmed_nmc_reward;
    private String unconfirmed_reward;
    private String username;
    @JsonIgnore
    private String wallet;
    private Workers workers;

    public Slush(
            @JsonProperty("confirmed_nmc_reward")
            String confirmed_nmc_reward,
            @JsonProperty("confirmed_reward")
            String confirmed_reward,
            @JsonProperty("estimated_reward")
            String estimated_reward,
            @JsonProperty("hashrate")
            String hashrate,
            @JsonProperty("unconfirmed_nmc_reward")
            String unconfirmed_nmc_reward,
            @JsonProperty("unconfirmed_reward")
            String unconfirmed_reward,
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

    public String getConfirmed_nmc_reward() {
        return this.confirmed_nmc_reward;
    }

    public String getConfirmed_reward() {
        return this.confirmed_reward;
    }

    public String getEstimated_reward() {
        return this.estimated_reward;
    }

    public String getHashrate() {
        return this.hashrate;
    }

    public String getNmc_send_threshold() {
        return this.nmc_send_threshold;
    }

    public String getRating() {
        return this.rating;
    }

    public String getSend_threshold() {
        return this.send_threshold;
    }

    public String getUnconfirmed_nmc_reward() {
        return this.unconfirmed_nmc_reward;
    }

    public void setUnconfirmed_nmc_reward(String unconfirmed_nmc_reward) {
        this.unconfirmed_nmc_reward = unconfirmed_nmc_reward;
    }

    public String getUnconfirmed_reward() {
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
