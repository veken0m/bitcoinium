package com.veken0m.miningpools.fiftybtc;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
	private Number active_workers;
	private Number confirmed_rewards;
	private String hash_rate;
	private Number payouts;

	public User(@JsonProperty("active_workers") Number active_workers,
			@JsonProperty("confirmed_rewards") Number confirmed_rewards,
			@JsonProperty("hash_rate") String hash_rate,
			@JsonProperty("payouts") Number payouts) {
		this.active_workers = active_workers;
		this.confirmed_rewards = confirmed_rewards;
		this.hash_rate = hash_rate;
		this.payouts = payouts;
	}

	public Number getActive_workers() {
		return this.active_workers;
	}

	public Number getConfirmed_rewards() {
		return this.confirmed_rewards;
	}

	public String getHash_rate() {
		return this.hash_rate;
	}

	public Number getPayouts() {
		return this.payouts;
	}
}
