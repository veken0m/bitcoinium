package com.veken0m.bitcoinium;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BitstampFragment extends BaseExchangeFragment {

	public BitstampFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.menu_fragment, container, false);
		buildMenu(view, BITSTAMP, true);
		return view;
	}

}
