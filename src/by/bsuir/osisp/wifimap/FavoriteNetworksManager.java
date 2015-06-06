package by.bsuir.osisp.wifimap;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class FavoriteNetworksManager {
	
	private ListView mDrawerList;
	private List<WifiNetwork> mFavoriteList = new ArrayList<WifiNetwork>();
	private ArrayAdapter<WifiNetwork> mFavoriteAdapter;
	
	
	public FavoriteNetworksManager(ListView drawerList, GoogleMapManager mapManager) {
		mDrawerList = drawerList;
		mFavoriteAdapter = new ArrayAdapter<WifiNetwork>(MainActivity.mInstance, R.layout.drawer_list_item, mFavoriteList);

		mDrawerList.setAdapter(mFavoriteAdapter);
	}
	
	
	public void addNetwork(WifiNetwork network) {
		mFavoriteAdapter.add(network);
	}
	
	
	public void remove(int index) {
		mFavoriteAdapter.remove(mFavoriteAdapter.getItem(index));
	}


	public WifiNetwork getItem(int position) {
		return mFavoriteAdapter.getItem(position);
	}
}
