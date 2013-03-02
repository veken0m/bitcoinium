package com.veken0m.miningpools.fiftybtc;

import java.util.List;

public class User{
   	private Number active_workers;
   	private Number confirmed_rewards;
   	private String hash_rate;
   	private Number payouts;

 	public Number getActive_workers(){
		return this.active_workers;
	}
	public void setActive_workers(Number active_workers){
		this.active_workers = active_workers;
	}
 	public Number getConfirmed_rewards(){
		return this.confirmed_rewards;
	}
	public void setConfirmed_rewards(Number confirmed_rewards){
		this.confirmed_rewards = confirmed_rewards;
	}
 	public String getHash_rate(){
		return this.hash_rate;
	}
	public void setHash_rate(String hash_rate){
		this.hash_rate = hash_rate;
	}
 	public Number getPayouts(){
		return this.payouts;
	}
	public void setPayouts(Number payouts){
		this.payouts = payouts;
	}
}
