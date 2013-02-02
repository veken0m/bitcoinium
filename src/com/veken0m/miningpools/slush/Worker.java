package com.veken0m.miningpools.slush;

public class Worker{
   	private boolean alive;
   	private Number hashrate;
   	private Number last_share;
   	private String score;
   	private Number shares;

 	public boolean getAlive(){
		return this.alive;
	}
	public void setAlive(boolean alive){
		this.alive = alive;
	}
 	public Number getHashrate(){
		return this.hashrate;
	}
	public void setHashrate(Number hashrate){
		this.hashrate = hashrate;
	}
 	public Number getLast_share(){
		return this.last_share;
	}
	public void setLast_share(Number last_share){
		this.last_share = last_share;
	}
 	public String getScore(){
		return this.score;
	}
	public void setScore(String score){
		this.score = score;
	}
 	public Number getShares(){
		return this.shares;
	}
	public void setShares(Number shares){
		this.shares = shares;
	}
}
