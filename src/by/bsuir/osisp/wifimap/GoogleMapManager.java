package by.bsuir.osisp.wifimap;

import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class GoogleMapManager implements GoogleMap.OnCameraChangeListener {
	
	private GoogleMap mMap;
	private List<WifiNetwork> mNetworks;

	
	public GoogleMapManager(MapFragment mapFragment) {
		mMap = mapFragment.getMap();
        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(53.901152, 27.553153), 18));
        mMap.setOnCameraChangeListener(this);
	}
	
	
	private void displayNetworksOnMap(List<WifiNetwork> networks) {
		if (networks == null)
			return;
		for (WifiNetwork network: networks) {
			MarkerOptions marker = new MarkerOptions()
					.position(new LatLng(network.getLattitude(), network.getLongitude()))
					.title(network.getSsid())
					.snippet(network.getPassword());
			mMap.addMarker(marker);
		}	
	}
	
	
	public void displayNetworks(final List<WifiNetwork> networks) {
		mNetworks = networks;
		MainActivity.runTask(new Runnable() {
			@Override
			public void run() {
				displayNetworksOnMap(networks);
			}
		});
	}


	@Override
	public void onCameraChange(CameraPosition position) {
		if (position.zoom <= 16.0)
			mMap.clear();
		else
			displayNetworksOnMap(mNetworks);
	}
}
