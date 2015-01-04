package com.xeiam.paint;

import com.xeiam.xchange.dto.trade.LimitOrder;

import java.util.ArrayList;
import java.util.List;

public class OrderCollider
{

    private List<OrderAtCoord> orders = new ArrayList<OrderAtCoord>();

    public void put(int x, int y, LimitOrder limitOrder)
    {
        OrderAtCoord orderAtCoord = new OrderAtCoord(x, y, limitOrder);
        orders.add(orderAtCoord);
    }


    public void clearOrders()
    {
        orders.clear();
    }

    public LimitOrder getHit(int x, int y, int touchRadius)
    {

        if (orders.size() == 0)
        {
            return null;
        }

        float minDist = 1E9f;
        int idx = -1;
        for (int i = 0; i < orders.size(); i++)
        {
            float dist = orders.get(i).getDistFrom(x, y);
            if (dist < minDist)
            {
                minDist = dist;
                idx = i;
            }
        }
        if (minDist < touchRadius)
        {
            System.out.println("dist=" + minDist + ", touchRadius=" + touchRadius);
            return orders.get(idx).getLimitOrder();
        }

        return null;
    }
}

class OrderAtCoord
{

    private final int x;
    private final int y;
    private final LimitOrder limitOrder;

    public OrderAtCoord(int x, int y, LimitOrder limitOrder)
    {
        this.x = x;
        this.y = y;
        this.limitOrder = limitOrder;
    }

    public float getDistFrom(int x, int y)
    {
        return (float) Math.sqrt(Math.pow((float) (this.x - x), 2) + Math.pow((float) (this.y - y), 2));
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public LimitOrder getLimitOrder()
    {
        return limitOrder;
    }
}


