package com.veken0m.mining.bitminter;

public class Workers {
    private boolean alive;
    private float hash_rate;
    private float last_work;
    private String name;
    private Work work;

    public boolean getAlive() {
        return this.alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public float getHash_rate() {
        return this.hash_rate;
    }

    public void setHash_rate(float hash_rate) {
        this.hash_rate = hash_rate;
    }

    public float getLast_work() {
        return this.last_work;
    }

    public void setLast_work(float last_work) {
        this.last_work = last_work;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Work getWork() {
        return this.work;
    }

    public void setWork(Work work) {
        this.work = work;
    }
}
