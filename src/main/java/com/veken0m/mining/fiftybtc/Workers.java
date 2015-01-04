package com.veken0m.mining.fiftybtc;

import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.ArrayList;
import java.util.List;

public class Workers
{
    private final List<Worker> workers = new ArrayList<>();

    @JsonAnySetter
    public void setWorker(String name, Worker worker)
    {

        this.workers.add(worker);
    }

    public List<Worker> getWorkers()
    {
        return workers;
    }
}
