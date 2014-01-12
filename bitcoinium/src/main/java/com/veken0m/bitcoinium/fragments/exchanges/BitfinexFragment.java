
package com.veken0m.bitcoinium.fragments.exchanges;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xeiam.xchange.AuthHelper;
import com.veken0m.bitcoinium.R;
import com.veken0m.bitcoinium.fragments.BaseExchangeFragment;

public class BitfinexFragment extends BaseExchangeFragment {

    protected static final String BITFINEX = "BitfinexExchange";

    public BitfinexFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.menu_fragment, container, false);
        buildMenu(view, BITFINEX, true);
        try {
            AuthHelper.trustAllCerts();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

}
