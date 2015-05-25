package by.bsuir.osisp.wifimap;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.MapFragment;


public class MainActivity extends Activity {

	private static MainActivity mInstance;
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
    public void onCreate(Bundle savedInstanceState) {
    	mInstance = this;

    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    	mMapManager = new GoogleMapManager((MapFragment)getFragmentManager().findFragmentById(R.id.map));
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
        		showWifiNetworks();
        		return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    
    private void showWifiNetworks() {
        mDbManager.connectToDatabase();
        mDbManager.queryWifiNetworks();
        mDbManager.disconnectFromDatabase();    	
    }
}

