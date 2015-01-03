package com.veken0m.mining.bitminter;

import java.util.List;

public class BitMinterData
{
    private float active_workers;
    private Balances balances;
    private float hash_rate;
    private String name;
    private float now;
    private Round_start round_start;
    private Shift shift;
    private List<Workers> workers;

    public float getActive_workers()
    {
        return this.active_workers;
    }

    public void setActive_workers(float active_workers)
    {
        this.active_workers = active_workers;
    }

    public Balances getBalances()
    {
        return this.balances;
    }

    public void setBalances(Balances balances)
    {
        this.balances = balances;
    }

    public float getHash_rate()
    {
        return this.hash_rate;
    }

    public void setHash_rate(float hash_rate)
    {
        this.hash_rate = hash_rate;
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public float getNow()
    {
        return this.now;
    }

    public void setNow(float now)
    {
        this.now = now;
    }

    public Round_start getRound_start()
    {
        return this.round_start;
    }

    public void setRound_start(Round_start round_start)
    {
        this.round_start = round_start;
    }

    public Shift getShift()
    {
        return this.shift;
    }

    public void setShift(Shift shift)
    {
        this.shift = shift;
    }

    public List<Workers> getWorkers()
    {
        return this.workers;
    }

    public void setWorkers(List<Workers> workers)
    {
        this.workers = workers;
    }
}
