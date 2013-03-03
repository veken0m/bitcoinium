package com.veken0m.miningpools.slush;

import org.codehaus.jackson.annotate.JsonProperty;

public class Worker{
   	private boolean alive;
   	private Number hashrate;
   	private Number last_share;
   	private String score;
   	private Number shares;
   	
	public Worker(
			@JsonProperty("alive") Boolean alive,
			@JsonProperty("hashrate") Number hashrate,
	        @JsonProperty("shares") Number shares,
	        @JsonProperty("score") String score,
	        @JsonProperty("last_share") Number last_share){
	   	this.alive = alive;
	   	this.hashrate = hashrate;
	   	this.last_share = last_share;
	   	this.shares = shares;
	   	this.score = score;
	   	this.last_share = last_share;
	}

 	public boolean getAlive(){
		return this.alive;
	}
 	public Number getHashrate(){
		return this.hashrate;
	}
 	public Number getLast_share(){
		return this.last_share;
	}
 	public String getScore(){
		return this.score;
	}
 	public Number getShares(){
		return this.shares;
	}
}
