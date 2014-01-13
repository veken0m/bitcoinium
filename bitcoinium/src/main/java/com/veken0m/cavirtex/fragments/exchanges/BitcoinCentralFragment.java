
package com.veken0m.cavirtex.fragments.exchanges;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.veken0m.cavirtex.R;
import com.veken0m.cavirtex.fragments.BaseExchangeFragment;

public class BitcoinCentralFragment extends BaseExchangeFragment {

    protected static final String BITCOINCENTRAL = "BitcoinCentralExchange";

    public BitcoinCentralFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.menu_fragment, container, false);
        buildMenu(view, BITCOINCENTRAL, true);
        return view;
    }

}
