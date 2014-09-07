package com.xeiam.paint;

import android.graphics.Paint;
import android.graphics.Paint.Style;

public class Pallet {
    public Paint gridTickPaint = new Paint();
    //Grid lines
    public Paint gridLinePaint = new Paint();
    //price text
    public Paint axisTitlePaint = new Paint();
    public Paint axisTextPaint = new Paint();
    //crosshair
    public Paint crossHairPaint = new Paint();
    //orders
    public Paint bidOrderPaint = new Paint();
    public Paint askOrderPaint = new Paint();
    public Paint orderOutlinePaint = new Paint();

    public Paint bidOrderPendingPaint = new Paint();
    public Paint askOrderPendingPaint = new Paint();

    //orderBook
    public Paint bidPaint = new Paint();
    public Paint askPaint = new Paint();
    public Paint bidPaintLast = new Paint();
    public Paint askPaintLast = new Paint();
    //ticker
    public Paint tickerLastPaint = new Paint();
    //exchange delay
    public Paint exchangeDelay = new Paint();
    //price plot
    public Paint tradePaint = new Paint();
    //waiting
    public Paint waitingTextPaint = new Paint();

    //bid ask change
    public Paint bidAskChangePaint = new Paint();

    //lastTicke
    public Paint lastTickPaint = new Paint();


    public Pallet() {
        gridTickPaint.setARGB(255, 100, 130, 100);
        gridTickPaint.setStrokeWidth(3);

        gridLinePaint.setARGB(255, 20, 40, 20);
        gridLinePaint.setStrokeWidth(1);

        crossHairPaint.setARGB(255, 255, 0, 0);
        crossHairPaint.setStrokeWidth(1f);

        axisTitlePaint.setARGB(255, 255, 255, 255);
        axisTitlePaint.setTextSize(20);
        axisTitlePaint.setTextAlign(Paint.Align.CENTER);

        axisTextPaint.setARGB(255, 255, 255, 255);
        axisTextPaint.setTextSize(18);

        bidOrderPaint.setARGB(255, 255, 128, 0);
        bidOrderPaint.setStyle(Style.FILL);

        askOrderPaint.setARGB(255, 0, 0, 255);
        askOrderPaint.setStyle(Style.FILL);

        orderOutlinePaint.setARGB(255, 255, 255, 255);
        orderOutlinePaint.setStyle(Style.STROKE);

        bidOrderPendingPaint.setARGB(255, 100, 100, 255);
        bidOrderPendingPaint.setStyle(Style.STROKE);

        askOrderPendingPaint.setARGB(255, 255, 255, 100);
        askOrderPendingPaint.setStyle(Style.STROKE);


        bidPaint.setARGB(255, 170, 80, 0);
        bidPaint.setStyle(Style.FILL);

        askPaint.setARGB(255, 0, 80, 170);
        askPaint.setStyle(Style.FILL);


        bidPaintLast.setARGB(255, 150, 150, 150);
        bidPaintLast.setStyle(Style.STROKE);
        bidPaintLast.setStrokeWidth(2);

        askPaintLast.setARGB(255, 150, 150, 150);
        askPaintLast.setStyle(Style.STROKE);
        askPaintLast.setStrokeWidth(2);

        tickerLastPaint.setARGB(255, 150, 150, 150);
        tickerLastPaint.setTextSize(75);
        tickerLastPaint.setTextAlign(Paint.Align.CENTER);


        exchangeDelay.setARGB(255, 150, 150, 150);
        exchangeDelay.setTextSize(30);
        exchangeDelay.setTextAlign(Paint.Align.CENTER);

        tradePaint.setARGB(255, 0, 255, 50);
        tradePaint.setStrokeWidth(2);
        tradePaint.setStyle(Style.STROKE);

        waitingTextPaint.setARGB(255, 255, 255, 255);
        waitingTextPaint.setTextSize(75);
        waitingTextPaint.setTextAlign(Paint.Align.CENTER);

        bidAskChangePaint.setARGB(255, 200, 200, 200);
        bidAskChangePaint.setTextSize(30);
        bidAskChangePaint.setTextAlign(Paint.Align.CENTER);

        lastTickPaint.setARGB(255, 255, 0, 0);
        lastTickPaint.setStyle(Style.FILL);
    }


}
