package com.veken0m.mining.eligius;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EligiusBalance {

    private final float confirmed;
    private final float expected;

    public EligiusBalance(
            @JsonProperty("confirmed")
            float confirmed,
            @JsonProperty("expected")
            float expected) {

        this.confirmed = confirmed;
        this.expected = expected;
    }

    public float getConfirmed() {
        return this.confirmed;
    }

    public float getExpected() {
        return this.expected;
    }
}
