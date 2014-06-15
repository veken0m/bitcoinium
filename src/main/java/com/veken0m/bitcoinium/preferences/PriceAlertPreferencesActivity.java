
package com.veken0m.bitcoinium.preferences;

import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.text.InputType;

import com.veken0m.bitcoinium.R;
import com.veken0m.bitcoinium.WidgetProvider;
import com.veken0m.bitcoinium.exchanges.Exchange;
import com.veken0m.utils.Constants;

public class PriceAlertPreferencesActivity extends BasePreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_price_alert);

        // Generate the alarm preferences
        PreferenceCategory alertSettingsPref = (PreferenceCategory) findPreference("alertSettingsPref");
        if(alertSettingsPref != null) {

            int numberWithDecimal = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL;
            String sAlertOverLimit = getString(R.string.pref_alert_over_summary);
            String sAlertUnderLimit = getString(R.string.pref_alert_under_summary);

            String[] sExchanges = getResources().getStringArray(getResources().getIdentifier("exchanges", "array", getPackageName()));
            for (String sExchange : sExchanges) {
                Exchange exchange = new Exchange(this, sExchange);

                PreferenceScreen alertThresholds = getPreferenceManager().createPreferenceScreen(this);
                alertThresholds.setTitle(getString(R.string.pref_alert_limits, sExchange));

                for (String sCurrency : exchange.getCurrencies()) {

                    PreferenceScreen alertLimits = getPreferenceManager().createPreferenceScreen(this);
                    alertLimits.setTitle(sCurrency);
                    String prefix = exchange.getIdentifier() + sCurrency.replace("/", "");

                    // Upper limit
                    EditTextPreference sHighInput = new EditTextPreference(this);
                    sHighInput.setDefaultValue("999999");
                    sHighInput.getEditText().setInputType(numberWithDecimal);
                    sHighInput.setKey(prefix + "Upper");
                    sHighInput.setTitle(getString(R.string.pref_alert_upper_limit, sExchange, sCurrency));
                    sHighInput.setSummary(sAlertOverLimit);
                    alertLimits.addPreference(sHighInput);

                    // Lower limit
                    EditTextPreference sLowInput = new EditTextPreference(this);
                    sLowInput.setDefaultValue("0");
                    sLowInput.getEditText().setInputType(numberWithDecimal);
                    sLowInput.setKey(prefix + "Lower");
                    sLowInput.setTitle(getString(R.string.pref_alert_lower_threshold, sExchange, sCurrency));
                    sLowInput.setSummary(sAlertUnderLimit);
                    alertLimits.addPreference(sLowInput);

                    alertThresholds.addPreference(alertLimits);
                }

                alertSettingsPref.addPreference(alertThresholds);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        // Tell the widgets to update preferences
        sendBroadcast(new Intent(this, WidgetProvider.class).setAction(Constants.REFRESH));
    }
}
