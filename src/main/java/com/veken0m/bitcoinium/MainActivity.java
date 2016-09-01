package com.veken0m.bitcoinium;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatDelegate;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.veken0m.bitcoinium.fragments.HomeMenuFragment;
import com.veken0m.bitcoinium.preferences.PreferencesActivity;
import com.veken0m.bitcoinium.preferences.PriceAlertPreferencesActivity;

public class MainActivity extends BaseActivity
{

    static
    {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HomeMenuFragment homeMenu = new HomeMenuFragment();
        homeMenu.setArguments(getIntent().getExtras());

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, homeMenu);
        fragmentTransaction.commit();

        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.ab_background_textured_bitcoinium));

        // Some hack to make widgets appear on devices without rebooting
        sendBroadcast(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_preferences:
                startActivity(new Intent(this, PreferencesActivity.class));
                return true;
            case R.id.action_alarm_preferences:
                startActivity(new Intent(this, PriceAlertPreferencesActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onPause()
    {
        super.onPause();
        // clear the extra
        getIntent().removeExtra("exchangeKey");
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        setIntent(intent);
    }
}
