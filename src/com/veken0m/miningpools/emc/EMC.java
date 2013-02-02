package com.veken0m.miningpools.emc;

import java.util.List;

public class EMC{
   	private String apikey;
   	private Data data;
   	private List workers;

 	public String getApikey(){
		return this.apikey;
	}
	public void setApikey(String apikey){
		this.apikey = apikey;
	}
 	public Data getData(){
		return this.data;
	}
	public void setData(Data data){
		this.data = data;
	}
 	public List getWorkers(){
		return this.workers;
	}
	public void setWorkers(List workers){
		this.workers = workers;
	}
}
