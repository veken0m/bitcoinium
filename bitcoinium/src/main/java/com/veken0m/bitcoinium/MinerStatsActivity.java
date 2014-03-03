
package com.veken0m.bitcoinium;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.veken0m.bitcoinium.fragments.mining.BTCGuildFragment;
import com.veken0m.bitcoinium.fragments.mining.BitMinterFragment;
import com.veken0m.bitcoinium.fragments.mining.DeepBitFragment;
import com.veken0m.bitcoinium.fragments.mining.EMCFragment;
import com.veken0m.bitcoinium.fragments.mining.EligiusFragment;
import com.veken0m.bitcoinium.fragments.mining.FiftyBTCFragment;
import com.veken0m.bitcoinium.fragments.mining.SlushFragment;
import com.veken0m.bitcoinium.preferences.MinerPreferenceActivity;
import com.veken0m.bitcoinium.preferences.PreferencesActivity;
import com.veken0m.utils.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
//import com.veken0m.utils.KarmaAdsUtils;

public class MinerStatsActivity extends SherlockFragmentActivity {

    private static String pref_emcKey = null;
    private static String pref_slushKey = null;
    private static String pref_bitminterKey = null;
    private static String pref_deepbitKey = null;
    private static String pref_50BTCKey = null;
    private static String pref_btcguildKey = null;
    private static String pref_eligiusKey = null;

    private static final int MIN_KEY_LENGTH = 20;

    private ActionBar actionbar = null;
    private Bundle extras = null;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ActionBar gets initiated and set to tabbed mode
        actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Add the pools that have API keys
        readPreferences(this);

        if (checkAtLeastOneKeySet()) {

            // If not API token set, switch to Preferences and ask User to enter
            // one
            int duration = Toast.LENGTH_LONG;
            CharSequence text = getString(R.string.enterAPIToken);

            Toast toast = Toast.makeText(this, text, duration);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

            Intent settingsActivity = new Intent(this, PreferencesActivity.class);
            startActivity(settingsActivity);
        }

        extras = getIntent().getExtras();
        // If the bundle is empty, Activity created from MainActivity
        // Attach all tabs
        if (extras == null)
            addTabs(actionbar);

        setContentView(R.layout.minerstats);
        new getDifficultyAsync().execute();
        actionbar.show();
        //KarmaAdsUtils.initAd(this);
    }

    private void addTabs(ActionBar actionbar, String poolkey) {

        actionbar.removeAllTabs();

        if (pref_bitminterKey.length() > MIN_KEY_LENGTH)
            addTab(actionbar, "BitMinter", new BitMinterFragment(), poolkey.equalsIgnoreCase("bitminter"));
        if (pref_deepbitKey.length() > MIN_KEY_LENGTH)
            addTab(actionbar, "DeepBit", new DeepBitFragment(), poolkey.equalsIgnoreCase("deepbit"));
        if (pref_slushKey.length() > MIN_KEY_LENGTH)
            addTab(actionbar, "Slush", new SlushFragment(), poolkey.equalsIgnoreCase("slush"));
        if (pref_emcKey.length() > MIN_KEY_LENGTH)
            addTab(actionbar, "EclipseMC", new EMCFragment(), poolkey.equalsIgnoreCase("eclipsemc"));
        if (pref_50BTCKey.length() > MIN_KEY_LENGTH)
            addTab(actionbar, "50BTC", new FiftyBTCFragment(), poolkey.equalsIgnoreCase("50btc"));
        if (pref_btcguildKey.length() > MIN_KEY_LENGTH)
            addTab(actionbar, "BTC Guild", new BTCGuildFragment(), poolkey.equalsIgnoreCase("btcguild"));
        if (pref_eligiusKey.length() > MIN_KEY_LENGTH)
            addTab(actionbar, "Eligius", new EligiusFragment(), poolkey.equalsIgnoreCase("eligius"));
    }

    private void addTabs(ActionBar actionbar) {

        actionbar.removeAllTabs();

        if (pref_bitminterKey.length() > MIN_KEY_LENGTH)
            addTab(actionbar, "BitMinter", new BitMinterFragment());
        if (pref_deepbitKey.length() > MIN_KEY_LENGTH)
            addTab(actionbar, "DeepBit", new DeepBitFragment());
        if (pref_slushKey.length() > MIN_KEY_LENGTH)
            addTab(actionbar, "Slush", new SlushFragment());
        if (pref_emcKey.length() > MIN_KEY_LENGTH)
            addTab(actionbar, "EclipseMC", new EMCFragment());
        if (pref_50BTCKey.length() > MIN_KEY_LENGTH)
            addTab(actionbar, "50BTC", new FiftyBTCFragment());
        if (pref_btcguildKey.length() > MIN_KEY_LENGTH)
            addTab(actionbar, "BTC Guild", new BTCGuildFragment());
        if (pref_eligiusKey.length() > MIN_KEY_LENGTH)
            addTab(actionbar, "Eligius", new EligiusFragment());
    }

    public void onResume() {
        super.onResume();

        if (extras != null) {
            String poolKey = extras.getString("poolKey");
            readPreferences(this);
            addTabs(actionbar, poolKey);
        }
    }

    private void addTab(ActionBar actionbar, String tabLabel, SherlockFragment viewFragment, boolean selectedTab) {

        ActionBar.Tab tab = actionbar.newTab().setText(tabLabel);
        tab.setTabListener(new MyTabsListener(viewFragment));
        actionbar.addTab(tab, selectedTab);
    }

    private void addTab(ActionBar actionbar, String tabLabel, SherlockFragment viewFragment) {

        ActionBar.Tab tab = actionbar.newTab().setText(tabLabel);
        tab.setTabListener(new MyTabsListener(viewFragment));
        actionbar.addTab(tab);
    }


    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    private boolean checkAtLeastOneKeySet() {

        return (pref_bitminterKey.length() <= MIN_KEY_LENGTH
                && pref_emcKey.length() <= MIN_KEY_LENGTH
                && pref_deepbitKey.length() <= MIN_KEY_LENGTH
                && pref_50BTCKey.length() <= MIN_KEY_LENGTH
                && pref_slushKey.length() <= MIN_KEY_LENGTH
                && pref_btcguildKey.length() <= MIN_KEY_LENGTH
                && pref_eligiusKey.length() <= MIN_KEY_LENGTH);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.minerstats);
    }

    class MyTabsListener implements ActionBar.TabListener {
        public final SherlockFragment fragment;

        public MyTabsListener(SherlockFragment fragment) {
            this.fragment = fragment;
        }

        @Override
        public void onTabReselected(Tab tab, FragmentTransaction ft) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_preferences:
                startActivity(new Intent(this, MinerPreferenceActivity.class));
                return true;
            case R.id.action_refresh:
                // TODO: implement refresh mechanism
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class getDifficultyAsync extends AsyncTask<Boolean, Void, Boolean> {

        String CurrentDifficulty = "";
        String NextDifficulty = "";
        String BlockCount = "";
        String NextRetarget = "";

        @Override
        protected Boolean doInBackground(Boolean... params) {

            try {
                HttpClient client = new DefaultHttpClient();

                // Get current difficulty
                HttpGet post = new HttpGet(
                        "http://blockexplorer.com/q/getdifficulty");
                HttpResponse response;
                response = client.execute(post);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                response.getEntity().getContent(), "UTF-8"));
                CurrentDifficulty = reader.readLine();

                // Get next difficulty
                post = new HttpGet("http://blockexplorer.com/q/estimate");
                response = client.execute(post);
                reader = new BufferedReader(new InputStreamReader(response
                        .getEntity().getContent(), "UTF-8"));
                NextDifficulty = reader.readLine();

                // Get block count
                post = new HttpGet("http://blockexplorer.com/q/getblockcount");
                response = client.execute(post);
                reader = new BufferedReader(new InputStreamReader(response
                        .getEntity().getContent(), "UTF-8"));
                BlockCount = reader.readLine();

                // Get next retarget
                post = new HttpGet("http://blockexplorer.com/q/nextretarget");
                response = client.execute(post);
                reader = new BufferedReader(new InputStreamReader(response
                        .getEntity().getContent(), "UTF-8"));
                NextRetarget = reader.readLine();
                reader.close();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                LinearLayout view = (LinearLayout) findViewById(R.id.minerStatslayout);
                Context context = getBaseContext();
                TextView tvCurrentDifficulty = new TextView(context);
                TextView tvNextDifficulty = new TextView(context);
                TextView tvBlockCount = new TextView(context);
                TextView tvNextRetarget = new TextView(context);

                try {
                    // TODO: move this to XML layout
                    tvCurrentDifficulty.setText(String.format(getString(R.string.currentDifficulty), Utils.formatDecimal(
                            Float.valueOf(CurrentDifficulty), 0, true)));
                    tvCurrentDifficulty.setGravity(Gravity.CENTER_HORIZONTAL);
                    tvCurrentDifficulty.setTextColor(Color.BLACK);
                    tvNextDifficulty.setText(String.format(getString(R.string.estimatedNextDifficulty), Utils.formatDecimal(
                            Float.valueOf(NextDifficulty), 0, true)));
                    tvNextDifficulty.setGravity(Gravity.CENTER_HORIZONTAL);
                    tvNextDifficulty.setTextColor(Color.BLACK);

                    tvBlockCount.setText(String.format(getString(R.string.blockCount), BlockCount));
                    tvBlockCount.setGravity(Gravity.CENTER_HORIZONTAL);
                    tvBlockCount.setTextColor(Color.BLACK);

                    tvNextRetarget.setText(String.format(getString(R.string.nextRetarget), Integer.parseInt(NextRetarget) - Integer.parseInt(BlockCount)));
                    tvNextRetarget.setGravity(Gravity.CENTER_HORIZONTAL);
                    tvNextRetarget.setTextColor(Color.BLACK);

                    if (Float.valueOf(NextDifficulty) < Float.valueOf(CurrentDifficulty))
                        tvNextDifficulty.setTextColor(Color.GREEN);
                    else
                        tvNextDifficulty.setTextColor(Color.RED);

                    view.addView(tvNextRetarget, 1);
                    view.addView(tvBlockCount, 1);
                    view.addView(tvNextDifficulty, 1);
                    view.addView(tvCurrentDifficulty, 1);
                } catch (Exception e) {
                    // Difficulty was NaN... don't display anything
                }
            }
        }

    }

    private static void readPreferences(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        pref_emcKey = prefs.getString("emcKey", "");
        pref_slushKey = prefs.getString("slushKey", "");
        pref_bitminterKey = prefs.getString("bitminterKey", "");
        pref_deepbitKey = prefs.getString("deepbitKey", "");
        pref_50BTCKey = prefs.getString("50BTCKey", "");
        pref_btcguildKey = prefs.getString("btcguildKey", "");
        pref_eligiusKey = prefs.getString("eligiusKey", "");
    }

    @Override
    public void onStart() {
        super.onStart();
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("googleAnalyticsPref", false)) {
            EasyTracker.getInstance(this).activityStart(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }

}
