
package com.veken0m.mining.slush;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Worker {
    private final boolean alive;
    private final float hashrate;
    private long last_share;
    private final String score;
    private final float shares;

    public Worker(@JsonProperty("alive")
                  Boolean alive,
                  @JsonProperty("hashrate")
                  float hashrate,
                  @JsonProperty("shares")
                  float shares,
                  @JsonProperty("score")
                  String score,
                  @JsonProperty("last_share")
                  long last_share) {
        this.alive = alive;
        this.hashrate = hashrate;
        this.last_share = last_share;
        this.shares = shares;
        this.score = score;
        this.last_share = last_share;
    }

    public boolean getAlive() {
        return this.alive;
    }

    public float getHashrate() {
        return this.hashrate;
    }

    public long getLast_share() {
        return this.last_share;
    }

    public String getScore() {
        return this.score;
    }

    public float getShares() {
        return this.shares;
    }
}
