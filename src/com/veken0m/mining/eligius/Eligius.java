
package com.veken0m.mining.eligius;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Eligius{
   	private TimeInterval threeHours;
   	private TimeInterval twoMinutes;
   	private TimeInterval twentyTwoMinutes;
   	private TimeInterval fourMinutes;
   	private TimeInterval twelveHours;
   	private ArrayList<String> intervals;
   	
    public Eligius(@JsonProperty("128")
            TimeInterval twoMinutes,
            @JsonProperty("256")
            TimeInterval fourMinutes,
            @JsonProperty("1350")
            TimeInterval twentyTwoMinutes,
            @JsonProperty("10800")
            TimeInterval threeHours,
            @JsonProperty("43200")
            TimeInterval twelveHours,
            @JsonProperty("intervals")
            ArrayList<String> intervals) {
        this.twoMinutes = twoMinutes;
        this.fourMinutes = fourMinutes;
        this.twentyTwoMinutes = twentyTwoMinutes;
        this.threeHours = threeHours;
        this.twelveHours = twelveHours;
        this.intervals = intervals;
    }
    
    public TimeInterval get43200(){
        return this.twelveHours;
    }
    
 	public TimeInterval get10800(){
		return this.threeHours;
	}
 	
 	public TimeInterval get128(){
		return this.twoMinutes;
	}
 	
 	public TimeInterval get1350(){
		return this.twentyTwoMinutes;
	}
 	
 	public TimeInterval get256(){
		return this.fourMinutes;
	}
 	
 	public ArrayList<String> getIntervalsNames(){
		return this.intervals;
	}
}
