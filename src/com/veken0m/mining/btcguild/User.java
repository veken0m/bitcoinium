
package com.veken0m.mining.btcguild;

import com.fasterxml.jackson.annotation.JsonProperty;


public class User {

    private Number user_id;
    private Number total_rewards;
    private Number paid_rewards;
    private Number unpaid_rewards;
    private Number past_24h_rewards;
    private Number total_rewards_nmc;
    private Number paid_rewards_nmc;
    private Number unpaid_rewards_nmc;
    private Number past_24h_rewards_nmc;
    
    public User(@JsonProperty("user_id") Number user_id, 
            @JsonProperty("total_rewards") Number total_rewards, 
            @JsonProperty("paid_rewards") Number paid_rewards, 
            @JsonProperty("unpaid_rewards") Number unpaid_rewards,
            @JsonProperty("past_24h_rewards") Number past_24h_rewards, 
            @JsonProperty("total_rewards_nmc") Number total_rewards_nmc, 
            @JsonProperty("paid_rewards_nmc") Number paid_rewards_nmc,
            @JsonProperty("unpaid_rewards_nmc") Number unpaid_rewards_nmc, 
            @JsonProperty("past_24h_rewards_nmc") Number past_24h_rewards_nmc) {
        
        this.user_id = user_id;
        this.total_rewards = total_rewards;
        this.paid_rewards = paid_rewards;
        this.unpaid_rewards = unpaid_rewards;
        this.past_24h_rewards = past_24h_rewards;
        this.total_rewards_nmc = total_rewards_nmc;
        this.paid_rewards_nmc = paid_rewards_nmc;
        this.unpaid_rewards_nmc = unpaid_rewards_nmc;
        this.past_24h_rewards_nmc = past_24h_rewards_nmc;
    }

    public Number getUser_id() {
        return user_id;
    }

    public Number getTotal_rewards() {
        return total_rewards;
    }

    public Number getPaid_rewards() {
        return paid_rewards;
    }

    public Number getUnpaid_rewards() {
        return unpaid_rewards;
    }

    public Number getPast_24h_rewards() {
        return past_24h_rewards;
    }

    public Number getTotal_rewards_nmc() {
        return total_rewards_nmc;
    }

    public Number getPaid_rewards_nmc() {
        return paid_rewards_nmc;
    }

    public Number getUnpaid_rewards_nmc() {
        return unpaid_rewards_nmc;
    }

    public Number getPast_24h_rewards_nmc() {
        return past_24h_rewards_nmc;
    }
}
