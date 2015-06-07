package by.bsuir.osisp.wifimap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;


@SuppressWarnings("deprecation")
public class MainActivity extends Activity implements 
					OnSharedPreferenceChangeListener, 
					OnItemClickListener {

	public static MainActivity mInstance;
	private SharedPreferences mSharedPrefences;
	
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	
	private GoogleMapManager mMapManager;
	private WifiDatabaseManager mDbManager;
	private FavoriteNetworksManager mFavNetManager;
		
	
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
	
	
	public void setActivityTheme(int resid) {
		// Тема должна быть установлена до setContentView() иначе установится плохо
		setTheme(resid);
		setContentView(R.layout.activity_main);
		// findViewById() должен быть вызван после setContentView() иначе NullPointerException
		ListView mDrawerList = (ListView) findViewById(R.id.left_drawer);
		if (resid == android.R.style.Theme_Holo_Light)
			mDrawerList.setBackgroundResource(R.drawable.background_holo_light);
		else if (resid == android.R.style.Theme_Holo)
			mDrawerList.setBackgroundResource(R.drawable.background_holo_dark);	
	}
	
	
	protected void setupFavoritesDrawer() {	
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);		
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle(R.string.favorite);
        mDrawerToggle = new ActionBarDrawerToggle(
				this,
				mDrawerLayout,
				R.drawable.ic_drawer,
				R.string.favorite,
				R.string.favorite);
	}
	
	
	@SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	mInstance = this;

    	mSharedPrefences = PreferenceManager.getDefaultSharedPreferences(this);
    	mSharedPrefences.registerOnSharedPreferenceChangeListener(this);

    	super.onCreate(savedInstanceState);
		setActivityTheme(Integer.valueOf(mSharedPrefences.getString(SettingsActivity.KEY_PREF_THEME, "")));
		setupFavoritesDrawer();

    	mMapManager = new GoogleMapManager(this);
    	mDbManager = new WifiDatabaseManager(mMapManager);
    	mFavNetManager = new FavoriteNetworksManager((ListView) findViewById(R.id.left_drawer), mMapManager);
    	
    	mDrawerList.setOnItemClickListener(this);

		mFavNetManager.load();
        mDbManager.connectToDatabase();
        mDbManager.queryWifiNetworks();
        mDbManager.disconnectFromDatabase();  
    }

	
	@Override
	protected void onDestroy() {
		mFavNetManager.save();
		super.onDestroy();
	};
	
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    
    @Override public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.add_to_favorite).setVisible(mMapManager.isInfoWindowVisible());
    	return super.onPrepareOptionsMenu(menu);
    };
    
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
    	
        switch (item.getItemId()) {
        	case R.id.add_to_favorite:
        		WifiNetwork selectedNetwork = mMapManager.getSelectedNetwork();
        		if (selectedNetwork != null)
        			mFavNetManager.addNetwork(selectedNetwork);
        		return true;
        	case R.id.settings:
        		Intent intent = new Intent(this, SettingsActivity.class);
        		startActivity(intent);
        		return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    
	@Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		WifiNetwork item = mFavNetManager.getItem(position);
		mMapManager.moveCamera(item.getPosition());
		mDrawerLayout.closeDrawers();
	}
}

