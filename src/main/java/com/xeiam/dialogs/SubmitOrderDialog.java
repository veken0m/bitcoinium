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


public class SubmitOrderDialog extends DialogFragment {

    float amount = 0;
    float price = 0;
    private View orderView;
    private OrderType orderType;


    public void set(OrderType orderType, float amount, float price) {
        this.amount = amount;
        this.price = price;
        this.orderType = orderType;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        orderView = inflater.inflate(R.layout.dialog_new_order, null);

        //set the amount
        ((TextView) orderView.findViewById(R.id.order_btc)).setText(XTraderActivity.fiveDecimalFormatter.format(amount));

        // Set currencies
        ((TextView) orderView.findViewById(R.id.new_order_transaction_currency)).setText(XTraderActivity.transactionCurrency.toUpperCase());
        ((TextView) orderView.findViewById(R.id.new_order_tradable_identifier)).setText(XTraderActivity.tradableIdentifier.toUpperCase());

        //set the price
        ((TextView) orderView.findViewById(R.id.order_price)).setText(XTraderActivity.twoDecimalFormatter.format(price));

        if (orderType == orderType.BID) {
            ((TextView) orderView.findViewById(R.id.order_type)).setText("BUY");
        } else {
            orderView.setBackgroundColor(Color.CYAN);
            ((TextView) orderView.findViewById(R.id.order_type)).setText("SELL");
        }

        // Add action buttons
        builder.setView(orderView).setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {
                try {
                    float amount = Float.parseFloat(((TextView) orderView.findViewById(R.id.order_btc)).getText().toString());
                    float price = Float.parseFloat(((TextView) orderView.findViewById(R.id.order_price)).getText().toString());
                    XTraderActivity.exchangeAccount.placeLimitOrder(price, amount, orderType, getActivity());
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        return builder.create();

    }

}
