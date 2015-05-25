package by.bsuir.osisp.wifimap;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;


public class MainActivity extends Activity {

	private static MainActivity mInstance;
	private GoogleMap mMap;
	private WifiDatabaseManager mDbManager = new WifiDatabaseManager();
    
	
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
        
        mMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
        mMap.setIndoorEnabled(true);
        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(-33.86997, 151.2089), 18));
        
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
        mDbManager.disconnectFromDatabase();    	
    }
}

