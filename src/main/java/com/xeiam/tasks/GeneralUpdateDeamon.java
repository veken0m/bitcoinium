package com.xeiam.tasks;

import android.os.AsyncTask;

import com.xeiam.xbtctrader.TraderActivity;

public class GeneralUpdateDeamon extends AsyncTask<String, Void, Boolean>
{

    public static final int PERIOD = 2500;
    public static String[] updateOrder = {"ticker", "orderbook", "ticker", "account", "ticker", "orders"};
    TraderActivity a;
    private boolean isActive = true;

    public GeneralUpdateDeamon(TraderActivity mainActivity)
    {
        this.a = mainActivity;
    }

    @Override
    protected Boolean doInBackground(String... params)
    {

        int idx = 0;
        while (isActive)
        {

            String query = updateOrder[idx % updateOrder.length];

            if (query.equals("ticker"))
            {
                TraderActivity.exchangeAccount.queryLastTicker();
            }
            else if (query.equals("orderbook"))
            {
                TraderActivity.exchangeAccount.queryOrderBook();
            }
            else if (query.equals("account"))
            {
                TraderActivity.exchangeAccount.queryRemoteAccountInfo();
            }
            else if (query.equals("orders"))
            {
                TraderActivity.exchangeAccount.queryOpenOrders();
            }

            sleep();
            idx++;
        }

        return true;
    }


    public boolean isActive()
    {
        return isActive;
    }

    public void setActive(boolean isActive)
    {
        this.isActive = isActive;
    }

    private void sleep()
    {
        try
        {
            Thread.sleep(PERIOD);
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
