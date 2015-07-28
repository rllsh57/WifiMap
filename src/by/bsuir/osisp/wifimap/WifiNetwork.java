package by.bsuir.osisp.wifimap;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;


@DatabaseTable(tableName = "wm_access_points")
public class WifiNetwork implements ClusterItem, Serializable {
	
	private static final long serialVersionUID = 2501569725623855689L;

	@DatabaseField(id=true)
	private int ap_id;
	@DatabaseField
	private String ap_ssid;
	@DatabaseField
	private String ap_password;
	@DatabaseField
	private double ap_lattitude;
	@DatabaseField
	private double ap_longiture;
	@DatabaseField
	private double ap_range;
	
	
	public WifiNetwork() {
	}
	
	
	public WifiNetwork(String ssid, String password) {
		ap_ssid = ssid;
		ap_password = password;
	}
	
	
	public String getSsid() {
		return ap_ssid;
	}
	
	
	public WifiNetwork setSsid(String ssid) {
		ap_ssid = ssid;
		return this;
	}
	
	
	public String getPassword() {
		return ap_password;
	}
	
	
	public double getLattitude() {
		return ap_lattitude;
	}


	public double getLongitude() {
		return ap_longiture;
	}


	@Override
	public LatLng getPosition() {
		return new LatLng(ap_lattitude, ap_longiture);
	}
	
	
	public String toString() {
		return ap_ssid;
	}


	public WifiNetwork setLatLng(double lattitude, double longitude) {
		ap_lattitude = lattitude;
		ap_longiture = longitude;
		return this;
	}


	public double getRange() {
		return ap_range;
	}


	public void setRange(double range) {
		ap_range = range;
	}


	public void connectTo() {
		WifiManager wifi_manager = (WifiManager) MainActivity.mInstance.getSystemService(Context.WIFI_SERVICE);
		if (wifi_manager == null) {
			MainActivity.makeToast("WifiManager service unavailable");
			return;
		}

		WifiConfiguration config = new WifiConfiguration();
		config.SSID = "\"" + ap_ssid + "\"";
		if (ap_password == null) 
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
		else {
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			config.preSharedKey = "\"" + ap_password + "\"";
		}
		
		int id = wifi_manager.addNetwork(config);
		if (id == -1) {
			MainActivity.makeToast("Network not added");
			return;
		}
		MainActivity.makeToast(wifi_manager.getConfiguredNetworks().size()+"");
		
		wifi_manager.enableNetwork(id, true);
	}
}
