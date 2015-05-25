package by.bsuir.osisp.wifimap;

import android.app.Activity;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;


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
//        run();
    }
    
    
    public static void makeToast(final String text) {
		Looper.prepare();
		Toast.makeText(mInstance, text, Toast.LENGTH_LONG).show();
		Looper.loop();
    }
    
    
    void run() {
    	Thread thr = new Thread() {
    		private JdbcConnectionSource mConnSource;
    		Dao<WifiNetwork, Integer> mDao;
    		
    		public void run() {
    			try {
    				doJob();
    			}
    			catch (Exception ex) {
    				makeToast(ex.toString());
    			}
    		}
    			
    		void doJob() throws Exception { 
    			mConnSource = new JdbcConnectionSource("jdbc:mysql://192.168.100.2:3306/test_wifi_map", "rllsh57", "107295");
    			mDao = DaoManager.createDao(mConnSource, WifiNetwork.class);
//    			TableUtils.createTable(mConnSource, WifiNetwork.class);
    			
    			WifiNetwork ap = new WifiNetwork("rllsh57", "some pass");
    			WifiNetwork ap2 = new WifiNetwork("lalka", "adwadawd");
    			mDao.create(ap);
    			mDao.create(ap2);
    			
    			mConnSource.close();
    			makeToast("Okay");
    		}
    	};
    	thr.start();
    }
}

