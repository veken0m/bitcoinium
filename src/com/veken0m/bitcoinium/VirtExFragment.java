package com.veken0m.bitcoinium;

import com.veken0m.bitcoinium.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class VirtExFragment extends BaseExchangeFragment {

	public VirtExFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.menu_fragment, container, false);
		buildMenu(view, VIRTEX);
		return view;
	}
}
