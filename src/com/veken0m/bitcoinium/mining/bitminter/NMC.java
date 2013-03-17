package com.veken0m.bitcoinium.mining.bitminter;

public class NMC {
	private Number checkpoint_accepted;
	private Number checkpoint_rejected;
	private Number round_accepted;
	private Number round_rejected;
	private Number total_accepted;
	private Number total_rejected;

	public Number getCheckpoint_accepted() {
		return this.checkpoint_accepted;
	}

	public void setCheckpoint_accepted(Number checkpoint_accepted) {
		this.checkpoint_accepted = checkpoint_accepted;
	}

	public Number getCheckpoint_rejected() {
		return this.checkpoint_rejected;
	}

	public void setCheckpoint_rejected(Number checkpoint_rejected) {
		this.checkpoint_rejected = checkpoint_rejected;
	}

	public Number getRound_accepted() {
		return this.round_accepted;
	}

	public void setRound_accepted(Number round_accepted) {
		this.round_accepted = round_accepted;
	}

	public Number getRound_rejected() {
		return this.round_rejected;
	}

	public void setRound_rejected(Number round_rejected) {
		this.round_rejected = round_rejected;
	}

	public Number getTotal_accepted() {
		return this.total_accepted;
	}

	public void setTotal_accepted(Number total_accepted) {
		this.total_accepted = total_accepted;
	}

	public Number getTotal_rejected() {
		return this.total_rejected;
	}

	public void setTotal_rejected(Number total_rejected) {
		this.total_rejected = total_rejected;
	}
}
