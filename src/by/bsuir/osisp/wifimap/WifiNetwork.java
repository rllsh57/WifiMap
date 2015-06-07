package by.bsuir.osisp.wifimap;

import java.io.Serializable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName = "wm_access_points")
public class WifiNetwork implements ClusterItem, Serializable {
	
	private static final long serialVersionUID = 2501569725623855689L;

	@DatabaseField(generatedId=true)
	private int ap_id;
	@DatabaseField
	private String ap_ssid;
	@DatabaseField
	private String ap_password;
	@DatabaseField
	private double ap_lattitude;
	@DatabaseField
	private double ap_longiture;
	
	
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
}
