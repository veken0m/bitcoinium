package com.xeiam.tasks;

import com.xeiam.business.ExchangeAccount;
import org.knowm.xchange.service.trade.TradeService;

import java.io.IOException;

public class CancelOrderTask implements Runnable
{

    private final ExchangeAccount exchangeAccount;
    private final String orderId;

    public CancelOrderTask(String orderId, ExchangeAccount exchangeAccount)
    {
        this.orderId = orderId;
        this.exchangeAccount = exchangeAccount;
    }

    public void go()
    {

        Thread t = new Thread(this);
        t.start();
    }

    @Override
    public void run()
    {
        TradeService tradeService = exchangeAccount.getTradeService();

        try
        {
            tradeService.cancelOrder(orderId);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        exchangeAccount.queryOpenOrders();
    }
}
