
package com.veken0m.mining.bitminter;

import com.fasterxml.jackson.annotation.JsonProperty;


public class Work {
    private final BTC bTC;
    private final NMC nMC;

    public Work(@JsonProperty("BTC")
    BTC bTC, @JsonProperty("NMC")
    NMC nMC) {

        this.bTC = bTC;
        this.nMC = nMC;
    }

    public BTC getBTC() {
        return this.bTC;
    }

    public NMC getNMC() {
        return this.nMC;
    }
}
