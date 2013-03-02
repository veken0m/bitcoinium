package com.veken0m.miningpools.slush;

import org.codehaus.jackson.annotate.JsonAnySetter;
import org.codehaus.jackson.annotate.JsonIgnore;

public class Slush{
   	private String confirmed_nmc_reward;
   	private String confirmed_reward;
   	private String estimated_reward;
   	private String hashrate;
   	private String nmc_send_threshold;
   	private String rating;
   	private String send_threshold;
   	private String unconfirmed_nmc_reward;
   	private String unconfirmed_reward;
   	private String username;
   	private String wallet;
   	@JsonIgnore
   	private Workers workers;

 	public String getConfirmed_nmc_reward(){
		return this.confirmed_nmc_reward;
	}
	public void setConfirmed_nmc_reward(String confirmed_nmc_reward){
		this.confirmed_nmc_reward = confirmed_nmc_reward;
	}
 	public String getConfirmed_reward(){
		return this.confirmed_reward;
	}
	public void setConfirmed_reward(String confirmed_reward){
		this.confirmed_reward = confirmed_reward;
	}
 	public String getEstimated_reward(){
		return this.estimated_reward;
	}
	public void setEstimated_reward(String estimated_reward){
		this.estimated_reward = estimated_reward;
	}
 	public String getHashrate(){
		return this.hashrate;
	}
	public void setHashrate(String hashrate){
		this.hashrate = hashrate;
	}
 	public String getNmc_send_threshold(){
		return this.nmc_send_threshold;
	}
	public void setNmc_send_threshold(String nmc_send_threshold){
		this.nmc_send_threshold = nmc_send_threshold;
	}
 	public String getRating(){
		return this.rating;
	}
	public void setRating(String rating){
		this.rating = rating;
	}
 	public String getSend_threshold(){
		return this.send_threshold;
	}
	public void setSend_threshold(String send_threshold){
		this.send_threshold = send_threshold;
	}
 	public String getUnconfirmed_nmc_reward(){
		return this.unconfirmed_nmc_reward;
	}
	public void setUnconfirmed_nmc_reward(String unconfirmed_nmc_reward){
		this.unconfirmed_nmc_reward = unconfirmed_nmc_reward;
	}
 	public String getUnconfirmed_reward(){
		return this.unconfirmed_reward;
	}
	public void setUnconfirmed_reward(String unconfirmed_reward){
		this.unconfirmed_reward = unconfirmed_reward;
	}
 	public String getUsername(){
		return this.username;
	}
	public void setUsername(String username){
		this.username = username;
	}
 	public String getWallet(){
		return this.wallet;
	}
	public void setWallet(String wallet){
		this.wallet = wallet;
	}
 	public Workers getWorkers(){
		return this.workers;
	}
 	@JsonAnySetter
	public void setWorkers(Workers workers){
		this.workers = workers;
	}
}
