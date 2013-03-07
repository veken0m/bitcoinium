package com.veken0m.bitcoinium;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Bitcoin24Fragment extends BaseExchangeFragment {

	public Bitcoin24Fragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.menu_fragment, container, false);
		buildMenu(view, BITCOIN24, true);
		return view;
	}

}
