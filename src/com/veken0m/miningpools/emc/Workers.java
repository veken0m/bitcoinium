package com.veken0m.miningpools.emc;

public class Workers{
   	private String hash_rate;
   	private String last_activity;
   	private String reset_shares;
   	private String round_shares;
   	private String total_shares;
   	private String worker_name;

 	public String getHash_rate(){
		return this.hash_rate;
	}
	public void setHash_rate(String hash_rate){
		this.hash_rate = hash_rate;
	}
 	public String getLast_activity(){
		return this.last_activity;
	}
	public void setLast_activity(String last_activity){
		this.last_activity = last_activity;
	}
 	public String getReset_shares(){
		return this.reset_shares;
	}
	public void setReset_shares(String reset_shares){
		this.reset_shares = reset_shares;
	}
 	public String getRound_shares(){
		return this.round_shares;
	}
	public void setRound_shares(String round_shares){
		this.round_shares = round_shares;
	}
 	public String getTotal_shares(){
		return this.total_shares;
	}
	public void setTotal_shares(String total_shares){
		this.total_shares = total_shares;
	}
 	public String getWorker_name(){
		return this.worker_name;
	}
	public void setWorker_name(String worker_name){
		this.worker_name = worker_name;
	}
}
