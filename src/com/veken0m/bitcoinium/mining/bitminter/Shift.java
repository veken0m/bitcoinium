package com.veken0m.bitcoinium.mining.bitminter;

public class Shift {
	private Number accepted;
	private Number rejected;
	private Number start;
	private Number total_score;
	private Number user_score;

	public Number getAccepted() {
		return this.accepted;
	}

	public void setAccepted(Number accepted) {
		this.accepted = accepted;
	}

	public Number getRejected() {
		return this.rejected;
	}

	public void setRejected(Number rejected) {
		this.rejected = rejected;
	}

	public Number getStart() {
		return this.start;
	}

	public void setStart(Number start) {
		this.start = start;
	}

	public Number getTotal_score() {
		return this.total_score;
	}

	public void setTotal_score(Number total_score) {
		this.total_score = total_score;
	}

	public Number getUser_score() {
		return this.user_score;
	}

	public void setUser_score(Number user_score) {
		this.user_score = user_score;
	}
}
