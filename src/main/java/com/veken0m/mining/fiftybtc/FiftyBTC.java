
package com.veken0m.mining.fiftybtc;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FiftyBTC {
    private final User user;
    private final Workers workers;

    public FiftyBTC(@JsonProperty("user") User user,
                    @JsonProperty("workers") Workers workers) {

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
