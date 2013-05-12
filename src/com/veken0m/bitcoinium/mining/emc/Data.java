
package com.veken0m.bitcoinium.mining.emc;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Data {
    private User user;

    public Data(@JsonProperty("user")
    User user) {
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }
}
