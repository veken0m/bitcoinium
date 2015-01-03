package com.xeiam.tasks;

import android.os.AsyncTask;

import com.xeiam.xbtctrader.XTraderActivity;

public class GeneralUpdateDeamon extends AsyncTask<String, Void, Boolean>
{

    public static final int PERIOD = 2500;
    public static String[] updateOrder = {"ticker", "orderbook", "ticker", "account", "ticker", "orders"};
    XTraderActivity a;
    private boolean isActive = true;

    public GeneralUpdateDeamon(XTraderActivity mainActivity)
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
                XTraderActivity.exchangeAccount.queryLastTicker();
            } else if (query.equals("orderbook"))
            {
                XTraderActivity.exchangeAccount.queryOrderBook();
            } else if (query.equals("account"))
            {
                XTraderActivity.exchangeAccount.queryRemoteAccountInfo();
            } else if (query.equals("orders"))
            {
                XTraderActivity.exchangeAccount.queryOpenOrders();
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
        } catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
