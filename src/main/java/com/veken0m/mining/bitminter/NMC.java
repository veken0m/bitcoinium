
package com.veken0m.mining.bitminter;

public class NMC {
    private float checkpoint_accepted;
    private float checkpoint_rejected;
    private float round_accepted;
    private float round_rejected;
    private float total_accepted;
    private float total_rejected;

    public float getCheckpoint_accepted() {
        return this.checkpoint_accepted;
    }

    public void setCheckpoint_accepted(float checkpoint_accepted) {
        this.checkpoint_accepted = checkpoint_accepted;
    }

    public float getCheckpoint_rejected() {
        return this.checkpoint_rejected;
    }

    public void setCheckpoint_rejected(float checkpoint_rejected) {
        this.checkpoint_rejected = checkpoint_rejected;
    }

    public float getRound_accepted() {
        return this.round_accepted;
    }

    public void setRound_accepted(float round_accepted) {
        this.round_accepted = round_accepted;
    }

    public float getRound_rejected() {
        return this.round_rejected;
    }

    public void setRound_rejected(float round_rejected) {
        this.round_rejected = round_rejected;
    }

    public float getTotal_accepted() {
        return this.total_accepted;
    }

    public void setTotal_accepted(float total_accepted) {
        this.total_accepted = total_accepted;
    }

    public float getTotal_rejected() {
        return this.total_rejected;
    }

    public void setTotal_rejected(float total_rejected) {
        this.total_rejected = total_rejected;
    }
}
