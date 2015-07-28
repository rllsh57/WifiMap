package by.bsuir.osisp.wifimap;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;


public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	
	public static final String KEY_PREF_DATABASE_SERVER = "pref_db_server";
	public static final String KEY_PREF_THEME = "pref_theme";

	private SharedPreferences mSharedPrefences;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {    	
    	mSharedPrefences = PreferenceManager.getDefaultSharedPreferences(this);
    	mSharedPrefences.registerOnSharedPreferenceChangeListener(this);

    	setTheme(Integer.valueOf(mSharedPrefences.getString(
				KEY_PREF_THEME,
				String.valueOf(R.style.AppTheme_Light))));

    	super.onCreate(savedInstanceState);
    	addPreferencesFromResource(R.xml.preferences);
    }


	@Override
	public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
		if (key.equals(SettingsActivity.KEY_PREF_THEME)) {
            recreate();
        }
	}


    @Override
    public void recreate() {
        if (Build.VERSION.SDK_INT >= 11) {
            super.recreate();
        }
    }
}