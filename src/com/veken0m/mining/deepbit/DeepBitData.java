
package com.veken0m.mining.deepbit;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class DeepBitData {

    private float confirmed_reward;
    private float hashrate;
    @JsonIgnore
    private boolean ipa;
    private float payout_history;
    private Workers workers;

    public DeepBitData(
            @JsonProperty("confirmed_reward")
            float confirmed_reward,
            @JsonProperty("hashrate")
            float hashrate,
            @JsonProperty("payout_history")
            float payout_history,
            @JsonProperty("workers")
            Workers workers) {
        this.confirmed_reward = confirmed_reward;
        this.hashrate = hashrate;
        this.payout_history = payout_history;
        this.workers = workers;
    }

    public float getConfirmed_reward() {
        return this.confirmed_reward;
    }

    public float getHashrate() {
        return this.hashrate;
    }

    public boolean getIpa() {
        return this.ipa;
    }

    public float getPayout_history() {
        return this.payout_history;
    }

    public Workers getWorkers() {
        return this.workers;
    }
}
