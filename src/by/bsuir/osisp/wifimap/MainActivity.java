package by.bsuir.osisp.wifimap;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;


public class MainActivity extends Activity implements OnSharedPreferenceChangeListener {

	private static MainActivity mInstance;
	private SharedPreferences mSharedPrefences;
	private GoogleMapManager mMapManager;
	private WifiDatabaseManager mDbManager;
		
	
    public static void makeToast(final String text) {
    	mInstance.runOnUiThread(new Runnable() {
    		@Override
    		public void run() {
        		Toast.makeText(mInstance, text, Toast.LENGTH_LONG).show();    			
    		}
    	});
    }
    
    
	public static void runTask(Runnable task) {
		mInstance.runOnUiThread(task);
	}

	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.equals(SettingsActivity.KEY_PREF_THEME))
			recreate();
	}
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	mInstance = this;

    	mSharedPrefences = PreferenceManager.getDefaultSharedPreferences(this);
    	mSharedPrefences.registerOnSharedPreferenceChangeListener(this);

    	super.onCreate(savedInstanceState);
		setTheme(Integer.valueOf(mSharedPrefences.getString(SettingsActivity.KEY_PREF_THEME, "")));
    	setContentView(R.layout.activity_main);

    	mMapManager = new GoogleMapManager(this);
    	mDbManager = new WifiDatabaseManager(mMapManager);
    }
    
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        	case R.id.show_wifi:
                mDbManager.connectToDatabase();
                mDbManager.queryWifiNetworks();
                mDbManager.disconnectFromDatabase();   
        		return true;
        	case R.id.settings:
        		Intent intent = new Intent(this, SettingsActivity.class);
        		startActivity(intent);
        		return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

