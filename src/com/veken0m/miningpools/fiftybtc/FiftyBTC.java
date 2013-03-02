package com.veken0m.miningpools.fiftybtc;

import java.util.List;

import org.codehaus.jackson.annotate.JsonAnySetter;
import org.codehaus.jackson.annotate.JsonIgnore;

public class FiftyBTC{
   	private User user;
   	@JsonIgnore
   	private Workers workers;

 	public User getUser(){
		return this.user;
	}
	public void setUser(User user){
		this.user = user;
	}
 	public Workers getWorkers(){
		return this.workers;
	}
 	@JsonAnySetter
	public void setWorkers(Workers workers){
		this.workers = workers;
	}
}
