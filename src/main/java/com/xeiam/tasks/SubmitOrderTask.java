package com.xeiam.tasks;

import android.app.Activity;
import android.widget.Toast;

import com.xeiam.business.ExchangeAccount;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.service.polling.trade.PollingTradeService;

import java.io.IOException;


public class SubmitOrderTask implements Runnable
{

    private final ExchangeAccount exchangeAccount;
    private final LimitOrder limitOrder;
    private final Activity activity;

    public SubmitOrderTask(LimitOrder limitOrder, ExchangeAccount exchangeAccount, Activity activity)
    {
        this.limitOrder = limitOrder;
        this.exchangeAccount = exchangeAccount;
        this.activity = activity;
    }

    public void go()
    {

        Thread t = new Thread(this);
        t.start();
    }

    @Override
    public void run()
    {
        System.out.println("SubmitOrderTask.doInBackGround()");
        PollingTradeService tradeService = exchangeAccount.getTradeService();
        System.out.println("placing order...");
        try
        {
            final String orderID = tradeService.placeLimitOrder(limitOrder);
            System.out.println("order placed. OrderID=" + orderID);
            activity.runOnUiThread(
                    new Runnable()
                    {
                        public void run()
                        {
                            Toast.makeText(activity.getApplicationContext(), "Order placed successfully. OrderID=" + orderID, Toast.LENGTH_LONG).show();
                        }
                    });
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (ExchangeException e)
        {
            e.printStackTrace();
            final String message = e.getMessage();
            activity.runOnUiThread(
                    new Runnable()
                    {
                        public void run()
                        {
                            Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        }
                    });
        }

        exchangeAccount.queryOpenOrders();
    }
}
