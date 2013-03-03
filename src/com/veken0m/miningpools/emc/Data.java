package com.veken0m.miningpools.emc;

import org.codehaus.jackson.annotate.JsonProperty;

public class Data {
	private User user;

	public Data(@JsonProperty("user") User user) {
		this.user = user;
	}

	public User getUser() {
		return this.user;
	}
}
