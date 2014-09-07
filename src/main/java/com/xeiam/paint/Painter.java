package com.xeiam.paint;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import com.xeiam.xbtctrader.MainView;
import com.xeiam.xbtctrader.XTraderActivity;
import com.xeiam.xchange.bitcoinium.dto.marketdata.BitcoiniumOrderbook;
import com.xeiam.xchange.bitcoinium.dto.marketdata.BitcoiniumTicker;
import com.xeiam.xchange.dto.Order.OrderType;
import com.xeiam.xchange.dto.trade.LimitOrder;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Painter {
    private static final String TAG = "Painter";

    private MainView mainView;
    private Canvas c;

    //offsets and scalars for mapping price and volume to screen.
    private double priceScalar;
    private double priceLowBound;
    private double depthScalar;
    private double ordergridsize;

    private float tOffSet;
    private float tScalar;


    //grid ticks
    private int numGridTicks = 10;
    private int arrowHeadSize = 10;
    private int gridTickLength = 20;
    private int crossHairRadius = 10;
    private int lastTickRadius = 5;
    private int orderRadius = 15;

    private OrderCollider orderCollider;

    private Path tradePath;
    private Path askPath;
    private Path bidPath;
    private Path askPathLast;
    private Path bidPathLast;

    private Pallet pallet;

    public Painter(MainView mainView) {
        this.mainView = mainView;
        this.pallet = new Pallet();
    }

    public void setBidAskPaths() {

        BitcoiniumOrderbook orderBook = XTraderActivity.exchangeAccount.getOrderBook();
        //set the scale
        if (orderBook.getAskVolumeList().get(orderBook.getAskVolumeList().size() - 1).floatValue() > orderBook.getBidVolumeList().get(orderBook.getBidVolumeList().size() - 1).floatValue()) {
            depthScalar = mainView.getHeight() / orderBook.getAskVolumeList().get(orderBook.getAskVolumeList().size() - 1).floatValue();
        } else {
            depthScalar = mainView.getHeight() / orderBook.getBidVolumeList().get(orderBook.getBidVolumeList().size() - 1).floatValue();
        }
        //paint the paths.
        this.askPath = getOrderBookPath(orderBook.getAskPriceList(), orderBook.getAskVolumeList(), true);
        this.bidPath = getOrderBookPath(orderBook.getBidPriceList(), orderBook.getBidVolumeList(), true);

        BitcoiniumOrderbook orderBookLast = XTraderActivity.exchangeAccount.getLastOrderBook();

        if (orderBookLast != null) {
            this.askPathLast = getOrderBookPath(orderBookLast.getAskPriceList(), orderBookLast.getAskVolumeList(), false);
            this.bidPathLast = getOrderBookPath(orderBookLast.getBidPriceList(), orderBookLast.getBidVolumeList(), false);
        }

    }

    private Path getOrderBookPath(List<BigDecimal> price, List<BigDecimal> volume, boolean isFilled) {

        int x;
        int y;

        Path p = new Path();
        //start on the y=axis.
        x = (int) ((price.get(0).floatValue() - priceLowBound) * priceScalar);
        y = mainView.getHeight();
        p.moveTo(x, y);

        //now plot the line
        for (int j = 0; j < price.size(); j++) {
            x = (int) ((price.get(j).floatValue() - priceLowBound) * priceScalar);
            y = (int) (mainView.getHeight() - volume.get(j).floatValue() * depthScalar);
            p.lineTo(x, y);
        }

        if (!isFilled) {
            return p;
        }

        //move to the edge. this makes sure the mountain is unbroken if its outside boundaries are withen the price scale
        if (x < mainView.getWidth() / 2) {
            x = 0;
        } else {
            x = mainView.getWidth();
        }
        p.lineTo(x, y);


        //now drop down
        //x is the same
        y = mainView.getHeight();//drop to bottom
        p.lineTo(x, mainView.getHeight());

        //and return to start
        x = (int) ((price.get(0).floatValue() - priceLowBound) * priceScalar);
        y = mainView.getHeight();
        p.moveTo(x, y);

        return p;

    }

    public void setTradeHistoryPath() {
        System.out.println("setTradeHistoryPath()");

        LinkedList<BitcoiniumTicker> trades = XTraderActivity.exchangeAccount.getTrades();

        System.out.println("setTradeHistoryPath(): trades.size()=" + trades.size());
        if (trades == null || trades.size() == 0) {
            return;
        }


        this.tOffSet = trades.get(0).getTimestamp();
        this.tScalar = (float) mainView.getHeight() / (trades.get(trades.size() - 1).getTimestamp() - tOffSet);
        tradePath = new Path();
        int x = (int) ((trades.get(0).getLast().floatValue() - priceLowBound) * priceScalar);
        int y = (int) ((trades.get(0).getTimestamp() - tOffSet) * tScalar);
        tradePath.moveTo(x, y);
        for (int i = 1; i < trades.size(); i++) {
            int tempy = (int) ((trades.get(i).getTimestamp() - tOffSet) * tScalar);
            x = (int) ((trades.get(i).getLast().floatValue() - priceLowBound) * priceScalar);
            if (tempy != y) {
                y = tempy;
                tradePath.lineTo(x, y);
            }
        }

        //add the last ticker
        BitcoiniumTicker ticker = XTraderActivity.exchangeAccount.getLastTicker();
        x = (int) ((ticker.getLast().floatValue() - priceLowBound) * priceScalar);
        y = (int) ((ticker.getTimestamp() - tOffSet) * tScalar);
        tradePath.lineTo(x, y);

    }

    private boolean okToPaint() {

        if (priceScalar == 0) {
            setScales();
            //System.out.println("OKTOPAINT: 1");
            return false;
        }
        if (tradePath == null) {
            setTradeHistoryPath();
            return false;
        }

        return true;
    }

    public void paint(Canvas c) {
        this.c = c;

        if (okToPaint()) {//if price scalars are set then data is in...can start to plot

            //ASK

            if (askPath != null && bidPath != null) {
                c.drawPath(askPath, pallet.askPaint);//ask wall
                c.drawPath(bidPath, pallet.bidPaint);//bid wall
            }

            if (askPathLast != null && bidPathLast != null) {
                c.drawPath(askPathLast, pallet.askPaintLast);// ask ghost line
                c.drawPath(bidPathLast, pallet.bidPaintLast);//bid  ghost line
            }


            c.drawPath(tradePath, pallet.tradePaint);//price history


            paintBorderGrid();
            paintLastTickerText();
            //paintBidAskChange();
            paintLastTick();
            paintOpenOrders();
            //paintPendingOrders();

            paintCrossHair();
        } else {//else just need to wait.
            //setScales();
            paintWaitingForData();
        }
    }


    public void setScales() {
        //System.out.println("SET SCALES CALLED!");
        BitcoiniumTicker ticker = XTraderActivity.exchangeAccount.getReferenceTicker();


        if (ticker == null) {
            return;
        }

        float priceWindow = mainView.getMainActivity().getPriceWindow();
        this.priceScalar = mainView.getWidth() / (2 * priceWindow * ticker.getLast().floatValue());
        this.priceLowBound = ticker.getLast().floatValue() * (1.0 - priceWindow);
        this.ordergridsize = mainView.getMainActivity().getOrderGridSize();


//		
//		System.out.println("priceWindow"+priceWindow);
//		System.out.println("priceScalar"+priceScalar);
//		System.out.println("priceLowBound"+priceLowBound);
//		System.out.println("ordergridsize"+ordergridsize);

    }

    private void paintWaitingForData() {
        c.drawText("Loading...", mainView.getWidth() / 2, mainView.getHeight() / 2, pallet.waitingTextPaint);
    }


//	private void paintBidAskChange(){
//		int dw=mainView.getWidth()/5;
//		
//		float askChange=XTraderActivity.exchangeAccount.getAskChange()/(GeneralUpdateDeamon.UPDATE_INTERVAL_ORDERBOOK_MS/1000);
//		float bidChange=XTraderActivity.exchangeAccount.getBidChange()/(GeneralUpdateDeamon.UPDATE_INTERVAL_ORDERBOOK_MS/1000);
//
//		if(askChange!=0){
//			 c.drawText(XTraderActivity.oneDecimalFormatter.format(askChange)+"BTC/s", mainView.getWidth()-dw, mainView.getHeight()-40, pallet.bidAskChangePaint);
//		}
//		if(bidChange!=0){
//			 c.drawText(XTraderActivity.oneDecimalFormatter.format(bidChange)+"BTC/s", dw, mainView.getHeight()-40, pallet.bidAskChangePaint);
//		}
//
//	}

    private void paintLastTickerText() {
        BitcoiniumTicker lastTicker = XTraderActivity.exchangeAccount.getLastTicker();
        if (lastTicker != null) {
            String text = XTraderActivity.fiatFormatter.format(lastTicker.getLast());
            c.drawText(text, mainView.getWidth() / 2, 60, pallet.tickerLastPaint);

            long exchangeDelay = XTraderActivity.exchangeAccount.getExchangeDelay();

            Paint paint = pallet.exchangeDelay;

            if (exchangeDelay > 0 && exchangeDelay < 30) {
                paint.setARGB(255, 0, 255, 0);
                paint.setTextSize(30);
            } else if (exchangeDelay > 30 && exchangeDelay < 60) {
                paint.setARGB(255, 255, 255, 0);
                paint.setTextSize(30);
            } else if (exchangeDelay > 60 && exchangeDelay < 90) {
                paint.setARGB(255, 255, 0, 0);
                paint.setTextSize(30);
            } else if (exchangeDelay > 90) {

                if (exchangeDelay % 2 == 0) {
                    paint.setARGB(255, 255, 0, 0);
                } else {
                    paint.setARGB(255, 155, 0, 0);
                }


                paint.setTextSize(40);
            }
            c.drawText((new Date(lastTicker.getTimestamp())).toLocaleString() + " (" + exchangeDelay + "s)", mainView.getWidth() / 2, 95, paint);
        }
    }

    private void paintOpenOrders() {

        List<LimitOrder> orders = XTraderActivity.exchangeAccount.getOpenOrders();
        if (orders == null || orders.size() == 0 && orderCollider != null) {
            orderCollider.clearOrders();
            return;
        }
        orderCollider = new OrderCollider();
        for (LimitOrder order : orders) {
            float price = order.getLimitPrice().floatValue();
            float amount = order.getTradableAmount().floatValue();

            int x = (int) ((price - priceLowBound) * priceScalar);
            int y = mainView.getHeight() - (int) ((amount * mainView.getHeight()) / (numGridTicks * ordergridsize));

            orderCollider.put(x, y, order);

            if (order.getType() == OrderType.ASK) {//SELL
                c.drawCircle(x, y, orderRadius, pallet.askOrderPaint);
            } else {//BUY
                c.drawCircle(x, y, orderRadius, pallet.bidOrderPaint);
            }

            c.drawCircle(x, y, orderRadius, pallet.orderOutlinePaint);


        }

    }

    //	private void paintPendingOrders(){
//
//		List<LimitOrder> orders=XTraderActivity.exchangeAccount.getPendingOrders();
//		if(orders==null || orders.size()==0){
//			return;
//		}
//		
//		for (int i = 0; i < orders.size(); i++) {
//			float price=orders.get(i).getLimitPrice().getAmount().floatValue();
//			float amount=orders.get(i).getTradableAmount().floatValue();
//
//			int x=(int)((price-priceLowBound)*priceScalar);
//			int y=mainView.getHeight()-(int)((amount*mainView.getHeight())/(numGridTicks*ordergridsize));
//
//			if(orders.get(i).getType()==OrderType.ASK){//SELL
//				c.drawCircle(x, y, orderRadius, pallet.askOrderPaint);
//			}else{//BUY
//				c.drawCircle(x, y, orderRadius, pallet.bidOrderPaint);
//			}
//		}
//		
//	}
    private void paintLastTick() {

        BitcoiniumTicker ticker = XTraderActivity.exchangeAccount.getLastTicker();

        if (ticker == null) {
            return;
        }

        int x = (int) ((ticker.getLast().floatValue() - priceLowBound) * priceScalar);
        int y = mainView.getHeight();
        c.drawCircle(x, y, lastTickRadius, pallet.lastTickPaint);

    }

    private void paintCrossHair() {

        //left hair
        c.drawLine(gridTickLength, mainView.getYTouch(), mainView.getXTouch(), mainView.getYTouch(), pallet.crossHairPaint);

        //left arrowhead
        Path leftArrowHead = new Path();
        leftArrowHead.moveTo(gridTickLength, mainView.getYTouch());
        leftArrowHead.lineTo(gridTickLength + arrowHeadSize, mainView.getYTouch() - arrowHeadSize / 2);
        leftArrowHead.lineTo(gridTickLength + arrowHeadSize, mainView.getYTouch() + arrowHeadSize / 2);
        leftArrowHead.lineTo(gridTickLength, mainView.getYTouch());
        c.drawPath(leftArrowHead, pallet.crossHairPaint);

        //down hair
        c.drawLine(mainView.getXTouch(), mainView.getYTouch(), mainView.getXTouch(), mainView.getHeight() - gridTickLength, pallet.crossHairPaint);

        //bottom arrowhead
        Path bottomArrowHead = new Path();
        bottomArrowHead.moveTo(mainView.getXTouch(), mainView.getHeight() - gridTickLength);
        bottomArrowHead.lineTo(mainView.getXTouch() - arrowHeadSize / 2, mainView.getHeight() - gridTickLength - arrowHeadSize);
        bottomArrowHead.lineTo(mainView.getXTouch() + arrowHeadSize / 2, mainView.getHeight() - gridTickLength - arrowHeadSize);
        bottomArrowHead.lineTo(mainView.getXTouch(), mainView.getHeight() - gridTickLength);
        c.drawPath(bottomArrowHead, pallet.crossHairPaint);


        //center dot
        c.drawCircle(mainView.getXTouch(), mainView.getYTouch(), crossHairRadius, pallet.crossHairPaint);
    }


    private void paintBorderGrid() {

        int w = mainView.getWidth();
        int h = mainView.getHeight();


        int dY = h / numGridTicks;
        int dX = w / numGridTicks;


        //grid lines horizontal
        for (int i = 1; i < numGridTicks; i++) {
            c.drawLine(0, i * dY, w, i * dY, pallet.gridLinePaint);
        }

        //grid lines vertical
        for (int i = 1; i < numGridTicks; i++) {
            c.drawLine(i * dX, h, i * dX, 0, pallet.gridLinePaint);
        }

        //bottom
        for (int i = 1; i < numGridTicks; i++) {
            c.drawLine(i * dX, h, i * dX, h - gridTickLength, pallet.gridTickPaint);
            float price = (float) ((float) i * dX / priceScalar + priceLowBound);
            c.drawText(XTraderActivity.fiatFormatter.format(price), i * dX + 3, h - 5, pallet.axisTextPaint);
        }

        //RIGHT ORDER BOOK

        int dw = w / 8;
        c.drawText("ORDER BOOK SIZE", w - dw, 20, pallet.axisTitlePaint);
        c.drawText("(kBTC)", w - dw, 40, pallet.axisTitlePaint);
        for (int i = 1; i < numGridTicks; i++) {
            c.drawLine(w, i * dY, w - gridTickLength, i * dY, pallet.gridTickPaint);
            float volume = (float) ((float) (h - i * dY) / depthScalar);
            volume /= 1E3;
            c.drawText(XTraderActivity.twoDecimalFormatter.format(volume), w - 50, i * dY - 5, pallet.axisTextPaint);
        }

        //LEFT YOUR ORDER VOLUME
        c.drawText("MY ORDER SIZE", dw, 20, pallet.axisTitlePaint);
        c.drawText("(BTC)", dw, 40, pallet.axisTitlePaint);
        for (int i = 1; i < numGridTicks; i++) {
            c.drawLine(0, i * dY, gridTickLength, i * dY, pallet.gridTickPaint);
            float amount = (float) ((numGridTicks - i) * ordergridsize);
            c.drawText(XTraderActivity.threeDecimalFormatter.format(amount), 5, (float) i * dY - 5f, pallet.axisTextPaint);
        }

    }


    public int getNumGridTicks() {
        return numGridTicks;
    }


    public void setNumGridTicks(int numGridTicks) {
        this.numGridTicks = numGridTicks;
    }


    public double getPriceScalar() {
        return priceScalar;
    }


    public double getPriceOffSet() {
        return priceLowBound;
    }


    public OrderCollider getOrderCollider() {
        return orderCollider;
    }


    public int getOrderRadius() {
        return orderRadius;
    }


}
