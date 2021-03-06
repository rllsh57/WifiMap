package by.bsuir.osisp.wifimap;

import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;


public class FavoriteNetworksManager {
	
	public static final String FAVORITE_STORAGE_FILE = "/data/data/by.bsuir.osisp.wifimap/files/favorite";
	
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
	
	
	public void save() {
		try {
			ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(new File(FAVORITE_STORAGE_FILE)));
			stream.writeObject(mFavoriteList);
			stream.close();
		} catch (Exception e) {
			Log.w(this.getClass().getSimpleName(), e.toString());
		}
	}
	
	
	public void load() {
		try {
			ObjectInputStream stream = new ObjectInputStream(new FileInputStream(new File(FAVORITE_STORAGE_FILE)));
			mFavoriteList.clear();
			for (WifiNetwork network: (List<WifiNetwork>) stream.readObject())
				mFavoriteAdapter.add(network);
			stream.close();
		} catch (Exception e) {
			Log.w(this.getClass().getSimpleName(), e.toString());
		}		
	}
}
