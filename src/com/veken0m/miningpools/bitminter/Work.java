
package com.veken0m.miningpools.bitminter;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class Work{
   	private BTC bTC;
   	private NMC nMC;
   	
   	public Work(@JsonProperty("BTC") BTC bTC, @JsonProperty("NMC") NMC nMC) {

   	    this.bTC = bTC;
   	    this.nMC = nMC;
   	  }

 	public BTC getBTC(){
		return this.bTC;
	}
 	public NMC getNMC(){
		return this.nMC;
	}
}
