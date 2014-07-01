package com.xeiam.business;

public class DatumTrade implements Comparable<DatumTrade>{

	private final long time;
	private final float price;

	public DatumTrade(long time,float price){
		this.price=price;
		this.time=time;
	}

	public float getPrice() {
		return price;
	}

	public long getTime() {
		return time;
	}

	@Override
	public int compareTo(DatumTrade another) {
		if(another.getTime()>this.getTime()){
			return -1;
		}else if(another.getTime()<this.getTime()){
			return 1;	
		}
		return 0;
	}

	@Override
	public String toString() {
		return price+"/"+time;
	}
	
}
