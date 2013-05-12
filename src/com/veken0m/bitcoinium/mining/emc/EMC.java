
package com.veken0m.bitcoinium.mining.emc;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class EMC {
    private String apikey;
    private Data data;
    private List<Workers> workers;

    public EMC(@JsonProperty("apikey")
    String apikey,
            @JsonProperty("data")
            Data data,
            @JsonProperty("workers")
            List<Workers> workers) {
        this.apikey = apikey;
        this.data = data;
        this.workers = workers;
    }

    public String getApikey() {
        return this.apikey;
    }

    public Data getData() {
        return this.data;
    }

    public List<Workers> getWorkers() {
        return this.workers;
    }
}
