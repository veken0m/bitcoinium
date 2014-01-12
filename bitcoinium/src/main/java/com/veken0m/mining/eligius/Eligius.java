
package com.veken0m.mining.eligius;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class Eligius {
    private final TimeInterval threeHours;
    private final TimeInterval twoMinutes;
    private final TimeInterval twentyTwoMinutes;
    private final TimeInterval fourMinutes;
    private final TimeInterval twelveHours;
    private final ArrayList<String> intervals;

    public Eligius(@JsonProperty("128")
                   TimeInterval twoMinutes,
                   @JsonProperty("256")
                   TimeInterval fourMinutes,
                   @JsonProperty("1350")
                   TimeInterval twentyTwoMinutes,
                   @JsonProperty("10800")
                   TimeInterval threeHours,
                   @JsonProperty("43200")
                   TimeInterval twelveHours,
                   @JsonProperty("intervals")
                   ArrayList<String> intervals) {
        this.twoMinutes = twoMinutes;
        this.fourMinutes = fourMinutes;
        this.twentyTwoMinutes = twentyTwoMinutes;
        this.threeHours = threeHours;
        this.twelveHours = twelveHours;
        this.intervals = intervals;
    }

    public TimeInterval get43200() {
        return this.twelveHours;
    }

    public TimeInterval get10800() {
        return this.threeHours;
    }

    public TimeInterval get128() {
        return this.twoMinutes;
    }

    public TimeInterval get1350() {
        return this.twentyTwoMinutes;
    }

    public TimeInterval get256() {
        return this.fourMinutes;
    }

    public ArrayList<String> getIntervalsNames() {
        return this.intervals;
    }
}
