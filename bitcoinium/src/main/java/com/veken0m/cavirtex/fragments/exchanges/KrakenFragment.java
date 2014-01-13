
package com.veken0m.cavirtex.fragments.exchanges;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.veken0m.cavirtex.R;
import com.veken0m.cavirtex.fragments.BaseExchangeFragment;

public class KrakenFragment extends BaseExchangeFragment {

    protected static final String KRAKEN = "KrakenExchange";

    public KrakenFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.menu_fragment, container, false);
        buildMenu(view, KRAKEN, true);
        return view;
    }

}
