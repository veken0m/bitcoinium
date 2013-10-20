
package com.veken0m.mining.eligius;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EligiusBalance{
    
    private BigDecimal confirmed;
    private BigDecimal expected;
    
   	public EligiusBalance(
   	        @JsonProperty("confirmed")
   	        BigDecimal confirmed, 
   	        @JsonProperty("expected")
   	        BigDecimal expected) {

        this.confirmed = confirmed;
        this.expected = expected;
    }
   	
 	public BigDecimal getConfirmed(){
		return this.confirmed;
	}

 	public BigDecimal getExpected(){
		return this.expected;
	}
}
