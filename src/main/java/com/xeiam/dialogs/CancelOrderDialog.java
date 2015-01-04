package com.xeiam.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.veken0m.bitcoinium.R;
import com.xeiam.xbtctrader.XTraderActivity;
import com.xeiam.xchange.dto.Order.OrderType;
import com.xeiam.xchange.dto.trade.LimitOrder;

public class CancelOrderDialog extends DialogFragment
{


    LimitOrder limitOrder;
    View orderView;

    public void set(LimitOrder limitOrder)
    {
        this.limitOrder = limitOrder;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        orderView = inflater.inflate(R.layout.dialog_cancel_order, null);


        //set the amount
        ((TextView) orderView.findViewById(R.id.cancel_order_amount)).setText("" + limitOrder.getTradableAmount().floatValue());

        //set the price
        ((TextView) orderView.findViewById(R.id.cancel_order_price)).setText("" + limitOrder.getLimitPrice().floatValue());

        if (limitOrder.getType() == OrderType.BID)
        {
            ((TextView) orderView.findViewById(R.id.cancel_order_type)).setText("BUY");
        }
        else
        {
            orderView.setBackgroundColor(Color.CYAN);
            ((TextView) orderView.findViewById(R.id.cancel_order_type)).setText("SELL");
        }

        //set the currency
        ((TextView) orderView.findViewById(R.id.cancel_order_fiat)).setText(limitOrder.getCurrencyPair().counterSymbol);

        //set the order ID
        ((TextView) orderView.findViewById(R.id.cancel_order_id)).setText(limitOrder.getId());

        // Add action buttons
        builder.setView(orderView).setPositiveButton("YES", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                System.out.println("CancelOrderDialog.onClick()");
                String orderId = ((TextView) orderView.findViewById(R.id.cancel_order_id)).getText().toString();
                System.out.println("orderId=" + orderId);
                XTraderActivity.exchangeAccount.cancelLimitOrder(orderId);
            }
        })
                .setNegativeButton("NO", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {

                    }
                });
        return builder.create();
    }
}
