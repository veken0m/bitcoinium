package com.veken0m.cavirtex;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuInflater;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

// -------------------------------------------------------------------------
/** 
 * @author Veken0m Based on Bitcoin-Alert Source by Dest
 * @version 1.1.3 Aug 12 2012
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

			// create the two fragments we want to use for display content
			SherlockFragment MtGoxFragment = new MtGoxFragment();
			SherlockFragment VirtexFragment = new VirtExFragment();

			// set the Tab listener. Now we can listen for clicks.
			MtGoxTab.setTabListener(new MyTabsListener(MtGoxFragment));
			VirtexTab.setTabListener(new MyTabsListener(VirtexFragment));

			// add the two tabs to the actionbar
			
			if(pref_favExchange.equalsIgnoreCase("mtgox")){
				actionbar.addTab(MtGoxTab);
				actionbar.addTab(VirtexTab);
			}
			if(pref_favExchange.equalsIgnoreCase("virtex")){
				actionbar.addTab(VirtexTab);
				actionbar.addTab(MtGoxTab);
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
			// ft.replace(R.id.virtexFragment, fragment);
			// Toast.makeText(StartActivity.appContext, "Reselected!",
			// Toast.LENGTH_LONG).show();
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			ft.replace(R.id.menuFragment, fragment);
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

	public boolean onPrepareOptionsMenu(Menu menu) {
		// preparation code here
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.preferences) {
			startActivity(new Intent(this, Preferences.class));
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
			
				
				pref_favExchange = pPrefs.getString("favExchangePref", "mtgox");
			}
		};

		prefs.registerOnSharedPreferenceChangeListener(prefListener);

		pref_favExchange = prefs.getString("favExchangePref", "mtgox");
	}

}
    
    

    

