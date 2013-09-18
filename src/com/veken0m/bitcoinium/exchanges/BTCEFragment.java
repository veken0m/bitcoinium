
package com.veken0m.bitcoinium.exchanges;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.veken0m.cavirtex.R;
import com.veken0m.cavirtex.BaseExchangeFragment;

public class BTCEFragment extends BaseExchangeFragment {

    public BTCEFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.menu_fragment, container, false);
        buildMenu(view, BTCE, true);
        return view;
    }

}
