package com.veken0m.miningpools.emc;

public class User{
   	private String blocks_found;
   	private String confirmed_rewards;
   	private Number estimated_rewards;
   	private String total_payout;
   	private String unconfirmed_rewards;

 	public String getBlocks_found(){
		return this.blocks_found;
	}
	public void setBlocks_found(String blocks_found){
		this.blocks_found = blocks_found;
	}
 	public String getConfirmed_rewards(){
		return this.confirmed_rewards;
	}
	public void setConfirmed_rewards(String confirmed_rewards){
		this.confirmed_rewards = confirmed_rewards;
	}
 	public Number getEstimated_rewards(){
		return this.estimated_rewards;
	}
	public void setEstimated_rewards(Number estimated_rewards){
		this.estimated_rewards = estimated_rewards;
	}
 	public String getTotal_payout(){
		return this.total_payout;
	}
	public void setTotal_payout(String total_payout){
		this.total_payout = total_payout;
	}
 	public String getUnconfirmed_rewards(){
		return this.unconfirmed_rewards;
	}
	public void setUnconfirmed_rewards(String unconfirmed_rewards){
		this.unconfirmed_rewards = unconfirmed_rewards;
	}
}
