package by.bsuir.osisp.wifimap;

import android.app.Activity;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;


public class MainActivity extends Activity {
    private GoogleMap mMap;
    private static MainActivity mInstance;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	mInstance = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
        mMap.setIndoorEnabled(true);
        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(-33.86997, 151.2089), 18));
        
        WifiDatabaseManager manager = new WifiDatabaseManager();
        manager.connectToDatabase();
        manager.disconnectFromDatabase();
    }
    
    
    public static void makeToast(final String text) {
		Looper.prepare();
		Toast.makeText(mInstance, text, Toast.LENGTH_LONG).show();
		Looper.loop();
    }
}

