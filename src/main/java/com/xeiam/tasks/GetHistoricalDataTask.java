package com.xeiam.tasks;

import com.xeiam.business.ExchangeAccount;


public class GetHistoricalDataTask implements Runnable {

    private final ExchangeAccount exchangeAccount;


    public GetHistoricalDataTask(ExchangeAccount exchangeAccount) {
        this.exchangeAccount = exchangeAccount;
    }

    public void go() {

        Thread t = new Thread(this);
        t.start();
    }

    @Override
    public void run() {
        exchangeAccount.queryTradeHistory();
        exchangeAccount.queryOrderBook();
    }

}
