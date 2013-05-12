
package com.veken0m.bitcoinium.mining.fiftybtc;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FiftyBTC {
    private User user;
    private Workers workers;

    public FiftyBTC(@JsonProperty("user")
    User user,
            @JsonProperty("workers")
            Workers workers) {
        this.user = user;
        this.workers = workers;
    }

    public User getUser() {
        return this.user;
    }

    public Workers getWorkers() {
        return this.workers;
    }
}
