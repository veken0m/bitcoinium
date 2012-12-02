package com.veken0m.bitcoinium;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.veken0m.bitcoinium.R;
import com.veken0m.miningpools.bitminter.BitMinterData;
import com.veken0m.miningpools.bitminter.Workers;
import com.veken0m.miningpools.deepbit.DeepBitData;

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

		// create the two fragments we want to use for display content
		SherlockFragment BitMinterFragment = new BitMinterFragment();
		SherlockFragment DeepBitFragment = new DeepBitFragment();

		// set the Tab listener. Now we can listen for clicks.
		BitMinterTab.setTabListener(new MyTabsListener(BitMinterFragment));
		DeepBitTab.setTabListener(new MyTabsListener(DeepBitFragment));
		readPreferences(getApplicationContext());
		// add the two tabs to the actionbar
		if (pref_favPool.equalsIgnoreCase("BitMinter")) {
			actionbar.addTab(BitMinterTab);
			actionbar.addTab(DeepBitTab);
		} else {
			actionbar.addTab(DeepBitTab);
			actionbar.addTab(BitMinterTab);
		}

		setContentView(R.layout.minerstats);
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

	static MinerData minerdata = new MinerData();

	final protected static String notAvailable = "N/A";
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

	public static class MinerData {
		private String RewardsNMC = "";
		private String RewardsBTC = "";
		private String Hashrate = "";
		private String Payout = "";
		private String Alive = "";
		private String Shares = "";
		private String Stales = "";
		private String Name = "";
		private String CurrentDifficulty = "";
		private String NextDifficulty = "";
		private List Workers = new ArrayList();
		private List WorkerNames = new ArrayList();

		public String getHashrate() {
			return this.Hashrate;
		}

		public String getRewardsNMC() {
			return this.RewardsNMC;
		}

		public String getRewardsBTC() {
			return this.RewardsBTC;
		}

		public String getPayout() {
			return this.Payout;
		}

		public String getAlive() {
			return this.Alive;
		}

		public String getShares() {
			return this.Shares;
		}

		public String getStales() {
			return this.Stales;
		}

		public String getName() {
			return this.Name;
		}

		public String getNextDifficulty() {
			return this.NextDifficulty;
		}

		public String getCurrentDifficulty() {
			return this.CurrentDifficulty;
		}

		public List getWorkers() {
			return this.Workers;
		}

		public List getWorkersNames() {
			return this.WorkerNames;
		}

		public void setDeepbitData(String APIToken)
				throws ClientProtocolException, IOException, EOFException {

			HttpClient client = new DefaultHttpClient();

			HttpGet post = new HttpGet("http://deepbit.net/api/" + APIToken);
			HttpResponse response = client.execute(post);
			ObjectMapper mapper = new ObjectMapper();
			DeepBitData data = mapper.readValue(new InputStreamReader(response
					.getEntity().getContent(), "UTF-8"), DeepBitData.class);

			this.RewardsBTC = "" + data.getConfirmed_reward().floatValue();
			this.Hashrate = "" + data.getHashrate().floatValue();
			this.RewardsNMC = notAvailable;
			this.Payout = "" + data.getPayout_history().floatValue();
			this.Alive = "" + data.getWorkers().getWorker(0).getAlive();
			this.Shares = "" + data.getWorkers().getWorker(0).getShares();
			this.Stales = "" + data.getWorkers().getWorker(0).getStales();
			this.Workers = data.getWorkers().getWorkers();
			this.WorkerNames = data.getWorkers().getNames();

		}

		public void setBitMinterData(String APIToken)
				throws ClientProtocolException, IOException, EOFException {

			HttpClient client = new DefaultHttpClient();

			// pref_bitminterKey = "M3IIJ5OCN2SQKRGRYVIXUFCJGG44DPNJ"; //Test
			// Key

			HttpGet post = new HttpGet("https://bitminter.com/api/users"
					+ "?key=" + APIToken);
			HttpResponse response = client.execute(post);

			ObjectMapper mapper = new ObjectMapper();
			BitMinterData data = mapper.readValue(new InputStreamReader(
					response.getEntity().getContent(), "UTF-8"),
					BitMinterData.class);

			List<Workers> workers = data.getWorkers();

			this.RewardsBTC = "" + data.getBalances().getBTC();
			this.Hashrate = "" + data.getHash_rate().toString();
			this.RewardsNMC = "" + data.getBalances().getNMC();
			this.Payout = "" + notAvailable;
			this.Alive = "" + workers.get(0).getAlive();
			this.Shares = workers.get(0).getWork().getBTC().getTotal_accepted()
					.toString();
			this.Stales = workers.get(0).getWork().getBTC().getTotal_rejected()
					.toString();
			this.Name = data.getName();
			this.Workers = data.getWorkers();
		}

		public void setDifficulty() throws ClientProtocolException,
				IOException, EOFException {

			HttpClient client = new DefaultHttpClient();
			HttpGet post = new HttpGet(
					"http://blockexplorer.com/q/getdifficulty");
			HttpResponse response = client.execute(post);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent(), "UTF-8"));
			this.CurrentDifficulty = reader.readLine();
			reader.close();

			post = new HttpGet("http://blockexplorer.com/q/estimate");
			response = client.execute(post);
			reader = new BufferedReader(new InputStreamReader(response
					.getEntity().getContent(), "UTF-8"));
			this.NextDifficulty = reader.readLine();
			reader.close();
		}

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
