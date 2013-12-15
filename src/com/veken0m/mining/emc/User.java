
package com.veken0m.mining.emc;

import com.fasterxml.jackson.annotation.JsonProperty;


public class User {
    private final long blocks_found;
    private final float confirmed_rewards;
    private final float estimated_rewards;
    private final float total_payout;
    private final float unconfirmed_rewards;

    public User(@JsonProperty("blocks_found")
    long blocks_found,
            @JsonProperty("confirmed_rewards")
            float confirmed_rewards,
            @JsonProperty("estimated_rewards")
            float estimated_rewards,
            @JsonProperty("total_payout")
            float total_payout,
            @JsonProperty("unconfirmed_rewards")
            float unconfirmed_rewards) {
        this.blocks_found = blocks_found;
        this.confirmed_rewards = confirmed_rewards;
        this.estimated_rewards = estimated_rewards;
        this.total_payout = total_payout;
        this.unconfirmed_rewards = unconfirmed_rewards;
    }

    public long getBlocks_found() {
        return this.blocks_found;
    }

    public float getConfirmed_rewards() {
        return this.confirmed_rewards;
    }

    public float getEstimated_rewards() {
        return this.estimated_rewards;
    }

    public float getTotal_payout() {
        return this.total_payout;
    }

    public float getUnconfirmed_rewards() {
        return this.unconfirmed_rewards;
    }
}
