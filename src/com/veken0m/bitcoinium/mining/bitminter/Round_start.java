package com.veken0m.bitcoinium.mining.bitminter;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Round_start {
	private float BTC;
	private float NMC;

	public Round_start(@JsonProperty("BTC") float BTC,
			@JsonProperty("NMC") float NMC) {

		this.BTC = BTC;
		this.NMC = NMC;
	}

	public float getBTC() {
		return this.BTC;
	}

	public void setBTC(float BTC) {
		this.BTC = BTC;
	}

	public Number getNMC() {
		return this.NMC;
	}

	public void setNMC(float NMC) {
		this.NMC = NMC;
	}
}
