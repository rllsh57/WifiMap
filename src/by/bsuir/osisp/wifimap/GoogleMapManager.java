package by.bsuir.osisp.wifimap;

import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class GoogleMapManager {
	
	private GoogleMap mMap;

	
	public GoogleMapManager(MapFragment mapFragment) {
		mMap = mapFragment.getMap();
        mMap.setIndoorEnabled(true);
        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(53.901152, 27.553153), 18));
	}
	
	
	private void displayNetworksOnMap(List<WifiNetwork> networks) {
		for (WifiNetwork network: networks) {
			MarkerOptions marker = new MarkerOptions()
					.position(new LatLng(network.getLattitude(), network.getLongitude())
			);
			mMap.addMarker(marker);
		}	
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
