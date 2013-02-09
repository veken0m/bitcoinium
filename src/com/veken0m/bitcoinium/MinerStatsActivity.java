package com.veken0m.bitcoinium;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class MinerStatsActivity extends SherlockFragmentActivity {

	static String pref_favPool;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// ActionBar gets initiated
		ActionBar actionbar = getSupportActionBar();
		// Tell the ActionBar we want to use Tabs.
		actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		ActionBar.Tab BitMinterTab = actionbar.newTab().setText("BitMinter");
		ActionBar.Tab DeepBitTab = actionbar.newTab().setText("DeepBit");
		ActionBar.Tab EMCTab = actionbar.newTab().setText("EMC");
		ActionBar.Tab SlushTab = actionbar.newTab().setText("Slush");

		// create the two fragments we want to use for display content
		SherlockFragment BitMinterFragment = new BitMinterFragment();
		SherlockFragment DeepBitFragment = new DeepBitFragment();
		SherlockFragment EMCFragment = new EMCFragment();
		SherlockFragment SlushFragment = new SlushFragment();

		// set the Tab listener. Now we can listen for clicks.
		BitMinterTab.setTabListener(new MyTabsListener(BitMinterFragment));
		DeepBitTab.setTabListener(new MyTabsListener(DeepBitFragment));
		EMCTab.setTabListener(new MyTabsListener(EMCFragment));
		SlushTab.setTabListener(new MyTabsListener(SlushFragment));
		readPreferences(getApplicationContext());
		// add the two tabs to the actionbar
		if (pref_favPool.equalsIgnoreCase("BitMinter")) {
			actionbar.addTab(BitMinterTab);
			actionbar.addTab(DeepBitTab);
		} else {
			actionbar.addTab(DeepBitTab);
			actionbar.addTab(BitMinterTab);
		}
		actionbar.addTab(EMCTab);
		actionbar.addTab(SlushTab);
		setContentView(R.layout.minerstats);
		try {
			setDifficulty(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		actionbar.show();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.minerstats);

	}

	class MyTabsListener implements ActionBar.TabListener {
		public SherlockFragment fragment;

		public MyTabsListener(SherlockFragment fragment) {
			this.fragment = fragment;
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			ft.replace(R.id.table_fragment, fragment);
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			ft.replace(R.id.table_fragment, fragment);
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			ft.remove(fragment);
		}

	}

	final Handler mMinerHandler = new Handler();
	protected Boolean connectionFail = false;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		// preparation code here
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.preferences) {
			startActivity(new Intent(this, PreferencesActivity.class));
		}
		return super.onOptionsItemSelected(item);
	}

	public void setDifficulty(Context context) throws ClientProtocolException,
			IOException, EOFException {

		// TODO: Move networking to separate thread
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		HttpClient client = new DefaultHttpClient();
		HttpGet post = new HttpGet("http://blockexplorer.com/q/getdifficulty");
		HttpResponse response = client.execute(post);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				response.getEntity().getContent(), "UTF-8"));
		String CurrentDifficulty = reader.readLine();
		reader.close();

		post = new HttpGet("http://blockexplorer.com/q/estimate");
		response = client.execute(post);
		reader = new BufferedReader(new InputStreamReader(response.getEntity()
				.getContent(), "UTF-8"));
		String NextDifficulty = reader.readLine();
		reader.close();

		LinearLayout view = (LinearLayout) findViewById(R.id.miner_difficulty);

		TextView tvCurrentDifficulty = new TextView(context);
		TextView tvNextDifficulty = new TextView(context);

		tvCurrentDifficulty
				.setText("Current Difficulty: "
						+ Utils.formatDecimal(Float.valueOf(CurrentDifficulty),
								0, true));
		tvNextDifficulty.setText("Estimated Next Difficulty: "
				+ Utils.formatDecimal(Float.valueOf(NextDifficulty), 0, true)
				+ "\n");

		if (Float.valueOf(NextDifficulty) < Float.valueOf(CurrentDifficulty)) {
			tvNextDifficulty.setTextColor(Color.GREEN);
		} else {
			tvNextDifficulty.setTextColor(Color.RED);
		}

		view.addView(tvCurrentDifficulty);
		view.addView(tvNextDifficulty);

	}

	protected static void readPreferences(Context context) {
		// Get the xml/preferences.xml preferences
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		SharedPreferences.OnSharedPreferenceChangeListener prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
			public void onSharedPreferenceChanged(SharedPreferences pPrefs,
					String key) {

				pref_favPool = pPrefs.getString("favpoolPref", "bitminter");
			}
		};

		prefs.registerOnSharedPreferenceChangeListener(prefListener);

		pref_favPool = prefs.getString("favpoolPref", "bitminter");
	}

}
