
package com.veken0m.bitcoinium.mining.emc;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
    private String blocks_found;
    private String confirmed_rewards;
    private Number estimated_rewards;
    private String total_payout;
    private String unconfirmed_rewards;

    public User(@JsonProperty("blocks_found")
    String blocks_found,
            @JsonProperty("confirmed_rewards")
            String confirmed_rewards,
            @JsonProperty("estimated_rewards")
            Number estimated_rewards,
            @JsonProperty("total_payout")
            String total_payout,
            @JsonProperty("unconfirmed_rewards")
            String unconfirmed_rewards) {
        this.blocks_found = blocks_found;
        this.confirmed_rewards = confirmed_rewards;
        this.estimated_rewards = estimated_rewards;
        this.total_payout = total_payout;
        this.unconfirmed_rewards = unconfirmed_rewards;
    }

    public String getBlocks_found() {
        return this.blocks_found;
    }

    public String getConfirmed_rewards() {
        return this.confirmed_rewards;
    }

    public Number getEstimated_rewards() {
        return this.estimated_rewards;
    }

    public String getTotal_payout() {
        return this.total_payout;
    }

    public String getUnconfirmed_rewards() {
        return this.unconfirmed_rewards;
    }
}
