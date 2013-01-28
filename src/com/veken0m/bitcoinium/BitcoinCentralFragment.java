package com.veken0m.bitcoinium;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BitcoinCentralFragment extends BaseExchangeFragment {

	public BitcoinCentralFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.menu_fragment, container, false);
		buildMenu(view, BITCOINCENTRAL, false);
		return view;
	}

}
