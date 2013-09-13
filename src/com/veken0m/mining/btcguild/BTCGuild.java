
package com.veken0m.mining.btcguild;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BTCGuild {
    private User user;
    private Workers workers;
    private Pool pool;

    public BTCGuild(@JsonProperty("user") User user,
            @JsonProperty("workers")Workers workers, 
            @JsonProperty("pool") Pool pool) {
        
        this.user = user;
        this.workers = workers;
        this.pool = pool;
    }

    public User getUser() {
        return this.user;
    }

    public Workers getWorkers() {
        return this.workers;
    }
    
    public Pool getPool() {
        return this.pool;
    }
}
