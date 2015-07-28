package by.bsuir.osisp.wifimap;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;


public class MainActivity extends FragmentActivity implements
					OnSharedPreferenceChangeListener, 
					OnItemClickListener,
					OnItemLongClickListener {

	public static MainActivity mInstance;
	public static SharedPreferences mSharedPrefences;

    private ActionBar mActionBar;
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
    public void recreate() {
        if (Build.VERSION.SDK_INT >= 11) {
            super.recreate();
        }
    }

	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.equals(SettingsActivity.KEY_PREF_THEME)) {
            recreate();
		}
    }
	
	
	protected void setupFavoritesDrawer() {	
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerList.setOnItemClickListener(this);
        mDrawerList.setOnItemLongClickListener(this);

        mDrawerToggle = new ActionBarDrawerToggle(
				this,
				mDrawerLayout,
				R.string.favorite,
				R.string.favorite) {
            @Override
            public void onDrawerSlide(View view, float offset) {
				supportInvalidateOptionsMenu();
				super.onDrawerSlide(view, offset);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (Build.VERSION.SDK_INT >= 14) {
            mActionBar = getActionBar();
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setTitle(R.string.favorite);
        }
	}
	

	@Override
    public void onCreate(Bundle savedInstanceState) {
    	mInstance = this;

    	mSharedPrefences = PreferenceManager.getDefaultSharedPreferences(this);
    	mSharedPrefences.registerOnSharedPreferenceChangeListener(this);

        super.onCreate(savedInstanceState);
        setTheme(Integer.valueOf(mSharedPrefences.getString(
                SettingsActivity.KEY_PREF_THEME,
                String.valueOf(R.style.AppTheme))));
        setContentView(R.layout.activity_main);
        setupFavoritesDrawer();

        mMapManager = new GoogleMapManager(this);
        mDbManager = new WifiDatabaseManager(mMapManager);
        mFavNetManager = new FavoriteNetworksManager(mDrawerList, mMapManager);

        mFavNetManager.load();
        mDbManager.queryLocal();
        mDbManager.importRemote();
	}


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();

        if ((Build.VERSION.SDK_INT >= 14) && (Build.VERSION.SDK_INT <= 20))
            mActionBar.setHomeAsUpIndicator(R.drawable.ic_drawer);
	}


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

	
	@Override
	protected void onDestroy() {
		mFavNetManager.save();
		super.onDestroy();
	}
	
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    
    @Override public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.add_to_favorite).setVisible(mMapManager.isInfoWindowVisible());
        menu.findItem(R.id.connect_to).setVisible(mMapManager.isInfoWindowVisible());
    	return super.onPrepareOptionsMenu(menu);
    }
    
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
    	
        WifiNetwork selectedNetwork;
        
        switch (item.getItemId()) {
        	case R.id.add_to_favorite:
        		selectedNetwork = mMapManager.getSelectedNetwork();
        		if (selectedNetwork != null)
        			mFavNetManager.addNetwork(selectedNetwork);
        		return true;
        	case R.id.connect_to:
        		selectedNetwork = mMapManager.getSelectedNetwork();
        		if (selectedNetwork != null)
        			selectedNetwork.connectTo();
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


	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		mFavNetManager.remove(position);
		return false;
	}
}

