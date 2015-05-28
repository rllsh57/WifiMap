package by.bsuir.osisp.wifimap;

import java.util.List;

import android.app.Activity;
import android.content.Context;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;


public class GoogleMapManager {
	
	private GoogleMap mMap;
	private ClusterManager<WifiNetwork> mClusterManager;

	
	private class WifiNetworkRenderer extends DefaultClusterRenderer<WifiNetwork> {

		private final BitmapDescriptor mWifiOpenIcon = BitmapDescriptorFactory.fromResource(R.drawable.ic_wifi_signal_4_light);
		private final BitmapDescriptor mWifiSecureIcon = BitmapDescriptorFactory.fromResource(R.drawable.ic_wifi_lock_signal_4_light);
		
        public WifiNetworkRenderer(Context context, GoogleMap map, ClusterManager<WifiNetwork> clusterManager) {
			super(context, map, clusterManager);
		}

		@Override
        protected void onBeforeClusterItemRendered(WifiNetwork network, MarkerOptions markerOptions) {
            markerOptions.title(network.getSsid())
                         .snippet(network.getPassword());
            if (network.getPassword() == null) 
            	markerOptions.icon(mWifiOpenIcon);
            else 
           		markerOptions.icon(mWifiSecureIcon);            	
		}
	};
	
	
	public GoogleMapManager(Activity activity) {
		MapFragment mapFragment = (MapFragment) activity.getFragmentManager().findFragmentById(R.id.map);
		
		mMap = mapFragment.getMap();
        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(53.901152, 27.553153), 18));

        mClusterManager = new ClusterManager<WifiNetwork>(activity, mMap);
        mClusterManager.setRenderer(new WifiNetworkRenderer(activity, mMap, mClusterManager));
        mMap.setOnCameraChangeListener(mClusterManager);
	}
	
	
	private void displayNetworksOnMap(List<WifiNetwork> networks) {
		if (networks == null)
			return;
		mClusterManager.clearItems();
		for (WifiNetwork network: networks) {
			mClusterManager.addItem(network);
		}
		mClusterManager.cluster();
	}
	
	
	public void displayNetworks(final List<WifiNetwork> networks) {
		MainActivity.runTask(new Runnable() {
			@Override
			public void run() {
				displayNetworksOnMap(networks);
			}
		});
	}
}
