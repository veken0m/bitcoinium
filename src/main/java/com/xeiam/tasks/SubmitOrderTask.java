package com.xeiam.tasks;

import android.widget.Toast;

import com.xeiam.business.ExchangeAccount;
import com.xeiam.xchange.dto.trade.LimitOrder;
import com.xeiam.xchange.service.polling.PollingTradeService;

import java.io.IOException;


public class SubmitOrderTask implements Runnable{

	private final ExchangeAccount exchangeAccount;
	private final LimitOrder limitOrder;

	public SubmitOrderTask(LimitOrder limitOrder,ExchangeAccount exchangeAccount){
		this.limitOrder=limitOrder;
		this.exchangeAccount=exchangeAccount;
	}

	public void go(){
		
		Thread t=new Thread(this);
		t.start();
	}

	@Override
	public void run() {
		System.out.println("SubmitOrderTask.doInBackGround()");
		PollingTradeService tradeService=exchangeAccount.getTradeService();
		System.out.println("placing order...");
        String orderID = null;
        try {
            orderID = tradeService.placeLimitOrder(limitOrder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("order placed. OrderID="+orderID);
		exchangeAccount.queryOpenOrders();
	}

}
