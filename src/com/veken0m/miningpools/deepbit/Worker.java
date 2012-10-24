
package com.veken0m.miningpools.deepbit;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties public class Worker{
   	private boolean alive;
   	private Number shares;
   	private Number stales;

 	public boolean getAlive(){
		return this.alive;
	}
	public void setAlive(boolean alive){
		this.alive = alive;
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
}
