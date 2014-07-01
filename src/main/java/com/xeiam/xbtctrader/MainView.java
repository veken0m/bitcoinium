package com.xeiam.xbtctrader;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.veken0m.bitcoinium.R;
import com.xeiam.paint.Painter;
import com.xeiam.xchange.dto.trade.LimitOrder;

public class MainView extends ImageView {

    private final int FRAME_RATE = 30;
    private XTraderActivity mainActivity;
    //init offscreen
    private int x_touch = -20;
    private int y_touch = -20;
    private Handler h;
    private Painter painter;

    private Runnable redrawThread = new Runnable() {
        @Override
        public void run() {
            invalidate();
        }
    };

    public MainView(Context context, AttributeSet attrs) {
        super(context, attrs);
        h = new Handler();
        painter = new Painter(this);
        setTouchListener();
    }


    private String getOrderString() {
        float[] orderCoords = getOrderCoords();
        float orderTotal = orderCoords[0] * orderCoords[1];

        StringBuilder builder = new StringBuilder();
        builder.append(XTraderActivity.threeDecimalFormatter.format(orderCoords[1]));
        builder.append("@");
        builder.append(XTraderActivity.fiatFormatter.format(orderCoords[0]));
        builder.append(" (" + XTraderActivity.fiatFormatter.format(orderTotal) + ") ");
        return builder.toString();
    }

    public float[] getOrderCoords() {

        float[] oc = new float[2];
        double y = getHeight() - y_touch;
        oc[0] = (float) x_touch / (float) painter.getPriceScalar() + (float) painter.getPriceOffSet();
        float ordergridsize = mainActivity.getOrderGridSize();
        oc[1] = (float) (y * painter.getNumGridTicks() * ordergridsize) / getHeight();
        return oc;
    }

    private void setTouchListener() {
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                x_touch = (int) event.getX();
                y_touch = (int) event.getY();

                View parent = (View) v.getParent();
                if (parent != null) {
                    TextView txtView = (TextView) parent.findViewById(R.id.order_coords);
                    txtView.setText(getOrderString());
                }

                LimitOrder limitOrder = null;
                if (painter != null && painter.getOrderCollider() != null) {
                    limitOrder = painter.getOrderCollider().getHit(x_touch, y_touch, painter.getOrderRadius());
                    if (limitOrder != null) {
                        mainActivity.vibrate(50);
                    }
                }

                if (event.getAction() == MotionEvent.ACTION_UP && limitOrder != null) {
                    mainActivity.cancle(limitOrder);
                }


                return true;
            }
        });
    }


    protected void onDraw(Canvas c) {
        painter.paint(c);

        h.postDelayed(redrawThread, FRAME_RATE);
    }

    public int getXTouch() {
        return x_touch;
    }

    public int getYTouch() {
        return y_touch;
    }

    public XTraderActivity getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(XTraderActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public Painter getPainter() {
        return painter;
    }

}