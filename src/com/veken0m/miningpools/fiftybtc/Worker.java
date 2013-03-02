package com.veken0m.miningpools.fiftybtc;

import java.util.List;

public class Worker{
   	private boolean alive;
   	private Number blocks_found;
   	private Number checkpoint_invalid;
   	private Number checkpoint_shares;
   	private Number checkpoint_stales;
   	private String hash_rate;
   	private Number invalid;
   	private Number last_share;
   	private Number shares;
   	private Number stales;
   	private Number total_invalid;
   	private Number total_shares;
   	private Number total_stales;
   	private String worker_name;

 	public boolean getAlive(){
		return this.alive;
	}
	public void setAlive(boolean alive){
		this.alive = alive;
	}
 	public Number getBlocks_found(){
		return this.blocks_found;
	}
	public void setBlocks_found(Number blocks_found){
		this.blocks_found = blocks_found;
	}
 	public Number getCheckpoint_invalid(){
		return this.checkpoint_invalid;
	}
	public void setCheckpoint_invalid(Number checkpoint_invalid){
		this.checkpoint_invalid = checkpoint_invalid;
	}
 	public Number getCheckpoint_shares(){
		return this.checkpoint_shares;
	}
	public void setCheckpoint_shares(Number checkpoint_shares){
		this.checkpoint_shares = checkpoint_shares;
	}
 	public Number getCheckpoint_stales(){
		return this.checkpoint_stales;
	}
	public void setCheckpoint_stales(Number checkpoint_stales){
		this.checkpoint_stales = checkpoint_stales;
	}
 	public String getHash_rate(){
		return this.hash_rate;
	}
	public void setHash_rate(String hash_rate){
		this.hash_rate = hash_rate;
	}
 	public Number getInvalid(){
		return this.invalid;
	}
	public void setInvalid(Number invalid){
		this.invalid = invalid;
	}
 	public Number getLast_share(){
		return this.last_share;
	}
	public void setLast_share(Number last_share){
		this.last_share = last_share;
	}
 	public Number getShares(){
		return this.shares;
	}
	public void setShares(Number shares){
		this.shares = shares;
	}
 	public Number getStales(){
		return this.stales;
	}
	public void setStales(Number stales){
		this.stales = stales;
	}
 	public Number getTotal_invalid(){
		return this.total_invalid;
	}
	public void setTotal_invalid(Number total_invalid){
		this.total_invalid = total_invalid;
	}
 	public Number getTotal_shares(){
		return this.total_shares;
	}
	public void setTotal_shares(Number total_shares){
		this.total_shares = total_shares;
	}
 	public Number getTotal_stales(){
		return this.total_stales;
	}
	public void setTotal_stales(Number total_stales){
		this.total_stales = total_stales;
	}
 	public String getWorker_name(){
		return this.worker_name;
	}
	public void setWorker_name(String worker_name){
		this.worker_name = worker_name;
	}
}
