package com.veken0m.mining.bitminter;

public class Shift
{
    private float accepted;
    private float rejected;
    private float start;
    private float total_score;
    private float user_score;

    public float getAccepted()
    {
        return this.accepted;
    }

    public void setAccepted(float accepted)
    {
        this.accepted = accepted;
    }

    public float getRejected()
    {
        return this.rejected;
    }

    public void setRejected(float rejected)
    {
        this.rejected = rejected;
    }

    public float getStart()
    {
        return this.start;
    }

    public void setStart(float start)
    {
        this.start = start;
    }

    public float getTotal_score()
    {
        return this.total_score;
    }

    public void setTotal_score(float total_score)
    {
        this.total_score = total_score;
    }

    public float getUser_score()
    {
        return this.user_score;
    }

    public void setUser_score(float user_score)
    {
        this.user_score = user_score;
    }
}
