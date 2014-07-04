package com.xeiam.tasks;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.widget.Toast;

import com.xeiam.business.ExchangeAccount;
import com.xeiam.xbtctrader.XTraderActivity;
import com.xeiam.xchange.ExchangeException;
import com.xeiam.xchange.dto.trade.LimitOrder;
import com.xeiam.xchange.service.polling.PollingTradeService;

import java.io.IOException;


public class SubmitOrderTask implements Runnable {

    private final ExchangeAccount exchangeAccount;
    private final LimitOrder limitOrder;
    private final Activity activity;

    public SubmitOrderTask(LimitOrder limitOrder, ExchangeAccount exchangeAccount, Activity activity) {
        this.limitOrder = limitOrder;
        this.exchangeAccount = exchangeAccount;
        this.activity = activity;
    }

    public void go() {

        Thread t = new Thread(this);
        t.start();
    }

    @Override
    public void run() {
        System.out.println("SubmitOrderTask.doInBackGround()");
        PollingTradeService tradeService = exchangeAccount.getTradeService();
        System.out.println("placing order...");
        String orderID = null;
        try {
            orderID = tradeService.placeLimitOrder(limitOrder);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ExchangeException e){
            e.printStackTrace();
            final String message = e.getMessage();
            activity.runOnUiThread(
                    new Runnable() {
                       public void run() {
                           Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_LONG).show();
                       }
                    });
        }
        System.out.println("order placed. OrderID=" + orderID);
        exchangeAccount.queryOpenOrders();
    }

}
