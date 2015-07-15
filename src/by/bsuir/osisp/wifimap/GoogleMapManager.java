package by.bsuir.osisp.wifimap;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.ClusterManager.OnClusterClickListener;
import com.google.maps.android.clustering.ClusterManager.OnClusterItemClickListener;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;


public class GoogleMapManager {
	
	private GoogleMap mMap;
	private ClusterManager<WifiNetwork> mClusterManager;
	private InfoWindowSpecarator mSpecarator = new InfoWindowSpecarator();

	private WifiNetwork mSelectedNetwork;
	private Circle mCircle;
	private Marker mMarker;

	private class WifiNetworkRenderer extends DefaultClusterRenderer<WifiNetwork> {

		private final BitmapDescriptor mWifiOpenIcon = BitmapDescriptorFactory.fromResource(R.drawable.ic_wifi_signal_4_light);
		private final BitmapDescriptor mWifiSecureIcon = BitmapDescriptorFactory.fromResource(R.drawable.ic_wifi_lock_signal_4_light);
		
        public WifiNetworkRenderer(Context context, GoogleMap map, ClusterManager<WifiNetwork> clusterManager) {
			super(context, map, clusterManager);
		}

		@Override
        protected void onBeforeClusterItemRendered(WifiNetwork network, MarkerOptions markerOptions) {
            markerOptions.title(network.getSsid())
                         .snippet(network.getPassword())
                         .anchor(0.5f, 0.5f);
            if (network.getPassword() == null) 
            	markerOptions.icon(mWifiOpenIcon);
            else 
           		markerOptions.icon(mWifiSecureIcon);
		}
		
        @Override
        protected boolean shouldRenderAsCluster(Cluster<WifiNetwork> cluster) {
            // При перерисовке кластера, infoWindow закрывается. Сдесь отслеживается это.
            boolean should = super.shouldRenderAsCluster(cluster);
            if (cluster.getItems().contains((Object) mSelectedNetwork) && should)
            	onInfoWindowHide();
            return should;
        }
	}
	
	private class InfoWindowSpecarator implements
						OnMapClickListener,
						OnClusterClickListener<WifiNetwork>,
						OnClusterItemClickListener<WifiNetwork>, OnMarkerClickListener {
		@Override
		public void onMapClick(LatLng arg0) {
			onInfoWindowHide();
		}
		@Override
		public boolean onClusterItemClick(WifiNetwork item) {
			onInfoWindowShow(item);
			return false;
		}
		@Override
		public boolean onClusterClick(Cluster<WifiNetwork> cluster) {
			onInfoWindowHide();
			return false;
		}		
		@Override
		public boolean onMarkerClick(Marker marker) {
			mMarker = marker;
			mClusterManager.onMarkerClick(marker);
			return false;
		}
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
	
	
	public GoogleMapManager(Activity activity) {
		MapFragment mapFragment = (MapFragment) activity.getFragmentManager().findFragmentById(R.id.map);

		mMap = mapFragment.getMap();
        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(53.901152, 27.553153), 18));

        mClusterManager = new ClusterManager<WifiNetwork>(activity, mMap);
        mClusterManager.setRenderer(new WifiNetworkRenderer(activity, mMap, mClusterManager));
        mClusterManager.setOnClusterItemClickListener(mSpecarator);
        mClusterManager.setOnClusterClickListener(mSpecarator);
        mMap.setOnCameraChangeListener(mClusterManager);
        mMap.setOnMarkerClickListener(mSpecarator);
        mMap.setOnMapClickListener(mSpecarator);
	}
	
	
	public void displayNetworks(final List<WifiNetwork> networks) {
		MainActivity.runTask(new Runnable() {
			@Override
			public void run() {
				displayNetworksOnMap(networks);
			}
		});
	}
	
	
	public void moveCamera(LatLng position) {
		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 18));
		onInfoWindowHide();
	}
	
	
	public boolean isInfoWindowVisible() {
		return mSelectedNetwork != null;
	}


	public WifiNetwork getSelectedNetwork() {
		return mSelectedNetwork;
	}
	
	
	public void onInfoWindowHide() {
		mSelectedNetwork = null;
		if (mCircle != null)
			mCircle.remove();
		if (mMarker != null)
			mMarker.hideInfoWindow();
	}
	
	
	public void onInfoWindowShow(WifiNetwork item) {
		mSelectedNetwork = item;
		if (mCircle != null)
			mCircle.remove();
		mCircle = mMap.addCircle(new CircleOptions()
							.center(item.getPosition())
							.radius(item.getRange())
							.fillColor(Color.argb(50, 0, 0, 255))
							.strokeColor(Color.argb(127, 0, 0, 255))
							.strokeWidth(2));
	}
}
