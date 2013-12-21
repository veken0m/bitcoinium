
package com.veken0m.mining.emc;

import com.fasterxml.jackson.annotation.JsonProperty;


public class Workers {
    private final String hash_rate;
    private final String last_activity;
    private final float reset_shares;
    private final float round_shares;
    private final float total_shares;
    private final String worker_name;

    public Workers(@JsonProperty("hash_rate")
    String hash_rate,
            @JsonProperty("last_activity")
            String last_activity,
            @JsonProperty("reset_shares")
            float reset_shares,
            @JsonProperty("round_shares")
            float round_shares,
            @JsonProperty("total_shares")
            float total_shares,
            @JsonProperty("worker_name")
            String worker_name) {
        this.hash_rate = hash_rate;
        this.last_activity = last_activity;
        this.reset_shares = reset_shares;
        this.round_shares = round_shares;
        this.total_shares = total_shares;
        this.worker_name = worker_name;
    }

    public String getHash_rate() {
        return this.hash_rate;
    }

    public String getLast_activity() {
        return this.last_activity;
    }

    public float getReset_shares() {
        return this.reset_shares;
    }

    public float getRound_shares() {
        return this.round_shares;
    }

    public float getTotal_shares() {
        return this.total_shares;
    }

    public String getWorker_name() {
        return this.worker_name;
    }
}
