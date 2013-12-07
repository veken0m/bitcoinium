
package com.veken0m.mining.deepbit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties
public class Worker {
    private boolean alive;
    private float shares;
    private float stales;

    public boolean getAlive() {
        return this.alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public Number getShares() {
        return this.shares;
    }

    public void setShares(float shares) {
        this.shares = shares;
    }

    public Number getStales() {
        return this.stales;
    }

    public void setStales(float stales) {
        this.stales = stales;
    }
}
