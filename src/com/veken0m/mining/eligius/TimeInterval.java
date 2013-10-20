
package com.veken0m.mining.eligius;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TimeInterval{
    
    private BigDecimal hashrate;
    private BigDecimal interval;
    private String interval_name;
    private BigDecimal shares;
    
   	public TimeInterval(
   	        @JsonProperty("hashrate")
   	        BigDecimal hashrate, 
   	        @JsonProperty("interval")
   	        BigDecimal interval, 
   	        @JsonProperty("interval_name")
   	        String interval_name,
   	        @JsonProperty("shares")
   	        BigDecimal shares) {

        this.hashrate = hashrate;
        this.interval = interval;
        this.interval_name = interval_name;
        this.shares = shares;
    }
   	
 	public BigDecimal getHashrate(){
		return this.hashrate;
	}

 	public BigDecimal getInterval(){
		return this.interval;
	}

 	public String getInterval_name(){
		return this.interval_name;
	}

 	public BigDecimal getShares(){
		return this.shares;
	}
}
