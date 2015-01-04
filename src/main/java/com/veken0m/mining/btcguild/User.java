package com.veken0m.mining.btcguild;

import com.fasterxml.jackson.annotation.JsonProperty;


public class User
{

    private final float user_id;
    private final float total_rewards;
    private final float paid_rewards;
    private final float unpaid_rewards;
    private final float past_24h_rewards;
    private final float total_rewards_nmc;
    private final float paid_rewards_nmc;
    private final float unpaid_rewards_nmc;
    private final float past_24h_rewards_nmc;

    public User(@JsonProperty("user_id") float user_id,
                @JsonProperty("total_rewards") float total_rewards,
                @JsonProperty("paid_rewards") float paid_rewards,
                @JsonProperty("unpaid_rewards") float unpaid_rewards,
                @JsonProperty("past_24h_rewards") float past_24h_rewards,
                @JsonProperty("total_rewards_nmc") float total_rewards_nmc,
                @JsonProperty("paid_rewards_nmc") float paid_rewards_nmc,
                @JsonProperty("unpaid_rewards_nmc") float unpaid_rewards_nmc,
                @JsonProperty("past_24h_rewards_nmc") float past_24h_rewards_nmc)
    {

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

    public float getUser_id()
    {
        return user_id;
    }

    public float getTotal_rewards()
    {
        return total_rewards;
    }

    public float getPaid_rewards()
    {
        return paid_rewards;
    }

    public float getUnpaid_rewards()
    {
        return unpaid_rewards;
    }

    public float getPast_24h_rewards()
    {
        return past_24h_rewards;
    }

    public float getTotal_rewards_nmc()
    {
        return total_rewards_nmc;
    }

    public float getPaid_rewards_nmc()
    {
        return paid_rewards_nmc;
    }

    public float getUnpaid_rewards_nmc()
    {
        return unpaid_rewards_nmc;
    }

    public float getPast_24h_rewards_nmc()
    {
        return past_24h_rewards_nmc;
    }
}
