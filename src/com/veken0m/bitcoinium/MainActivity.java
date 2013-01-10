package com.veken0m.bitcoinium;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.veken0m.bitcoinium.R;

// -------------------------------------------------------------------------
/**
 * @author Veken0m Based on Bitcoin-Alert Source by Dest
 * @version 1.3.0 Oct 20 2012
 */
public class MainActivity extends SherlockFragmentActivity {
	static String pref_favExchange;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		readPreferences(getApplicationContext());

		// ActionBar gets initiated
		ActionBar actionbar = getSupportActionBar();

		// Tell the ActionBar we want to use Tabs.
		actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		ActionBar.Tab MtGoxTab = actionbar.newTab().setIcon(
				R.drawable.mtgoxlogo);
		ActionBar.Tab VirtexTab = actionbar.newTab().setIcon(
				R.drawable.virtexlogo);
		ActionBar.Tab BTCETab = actionbar.newTab().setIcon(R.drawable.btcelogo);
		ActionBar.Tab BitstampTab = actionbar.newTab().setIcon(
				R.drawable.bitstamplogo);
		ActionBar.Tab CampBXTab = actionbar.newTab().setIcon(
				R.drawable.campbxlogo);

		// create the fragments we want to use for display content
		SherlockFragment MtGoxFragment = new MtGoxFragment();
		SherlockFragment VirtexFragment = new VirtExFragment();
		SherlockFragment BTCEFragment = new BTCEFragment();
		SherlockFragment BitstampFragment = new BitstampFragment();
		SherlockFragment CampBXFragment = new CampBXFragment();

		// set the Tab listener. Now we can listen for clicks.
		MtGoxTab.setTabListener(new MyTabsListener(MtGoxFragment));
		VirtexTab.setTabListener(new MyTabsListener(VirtexFragment));
		BTCETab.setTabListener(new MyTabsListener(BTCEFragment));
		BitstampTab.setTabListener(new MyTabsListener(BitstampFragment));
		CampBXTab.setTabListener(new MyTabsListener(CampBXFragment));

		// add the tabs to the actionbar
		actionbar.addTab(MtGoxTab);
		actionbar.addTab(VirtexTab);
		actionbar.addTab(BTCETab);
		actionbar.addTab(BitstampTab);
		actionbar.addTab(CampBXTab);

		try{
		actionbar.setSelectedNavigationItem(Integer.parseInt(pref_favExchange));
		} catch (Exception e){
			
		}

		actionbar.show();

	}

	class MyTabsListener implements ActionBar.TabListener {
		public SherlockFragment fragment;

		public MyTabsListener(SherlockFragment fragment) {
			this.fragment = fragment;
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			//ft.replace(R.id.fragment_lay, fragment);
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			ft.add(R.id.fragment_lay, fragment);
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			ft.remove(fragment);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// item.setIcon(android.R.drawable.ic_menu_preferences);
		if (item.getItemId() == R.id.preferences) {
			startActivity(new Intent(this, PreferencesActivity.class));
		}
		return super.onOptionsItemSelected(item);
	}

	protected static void readPreferences(Context context) {
		// Get the xml/preferences.xml preferences
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		SharedPreferences.OnSharedPreferenceChangeListener prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
			public void onSharedPreferenceChanged(SharedPreferences pPrefs,
					String key) {

				pref_favExchange = pPrefs.getString("favExchangePref", "0");
			}
		};

		prefs.registerOnSharedPreferenceChangeListener(prefListener);

		pref_favExchange = prefs.getString("favExchangePref", "0");
	}

}
