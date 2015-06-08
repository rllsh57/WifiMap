package by.bsuir.osisp.wifimap;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.android.gms.maps.model.VisibleRegion;
import com.j256.ormlite.android.AndroidConnectionSource;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;


public class WifiDatabaseManager {
	
	private static final String DATABASE_LOGIN = "rllsh57";
	private static final String DATABASE_PASS = "107295";
	private static final String SQLITE_DATABASE_DIR = "/data/data/by.bsuir.osisp.wifimap/databases/";
	private static final String SQLITE_DATABASE_FILE = "wifi_map.db";
	
	private ConnectionSource mLocalSource;
	private ConnectionSource mRemoteSource;
	private Dao<WifiNetwork, Integer> mRemoteDao;
	private Dao<WifiNetwork, Integer> mLocalDao;
	private Executor mExecutor = Executors.newSingleThreadExecutor();
	private GoogleMapManager mMapManager;
	
	private VisibleRegion mRegion;
	
	private Runnable mQueryLocal = new ExceptionSafeTask() {		
		@Override
		protected void task() throws Exception {
			openLocalConnection();
			
			List<WifiNetwork> networks = mLocalDao.queryForAll();
			mMapManager.displayNetworks(networks);
			
			mLocalSource.close();
			
		}
	};
	
	private Runnable mQueryRemote = new ExceptionSafeTask() {		
		@Override
		protected void task() throws Exception {
			openRemoteConnection();
			
			List<WifiNetwork> networks = mRemoteDao.queryForAll();
			mMapManager.displayNetworks(networks);
		
			mRemoteSource.close();
		}
	};
	
	private Runnable mImportRemote = new ExceptionSafeTask() {
		@Override
		protected void task() throws Exception {
			openLocalConnection();
			openRemoteConnection();
			
			try {
				TableUtils.createTable(mLocalSource, WifiNetwork.class);
			}
			catch (Exception e) {
				Log.w(this.getClass().getSimpleName(), e.toString());
			}

			List<WifiNetwork> data = mRemoteDao.queryForAll();
			for (WifiNetwork network: data) {
				mLocalDao.createOrUpdate(network);
			}
			
			mLocalSource.close();
			mRemoteSource.close();			
		}
	};
	
	private Runnable mQueryRegion = new ExceptionSafeTask() {
		@Override
		protected void task() throws Exception {
			openLocalConnection();
			
			VisibleRegion region = mRegion;
			double width = region.farRight.latitude - region.nearLeft.latitude;
			double height = region.farRight.longitude - region.nearLeft.longitude;

			QueryBuilder<WifiNetwork, Integer> builder = mLocalDao.queryBuilder();
			builder.where().
					between("ap_lattitude", region.nearLeft.latitude - width, region.farRight.latitude + width).and().
					between("ap_longiture", region.nearLeft.longitude - height, region.farRight.longitude + height);
			List<WifiNetwork> data = mLocalDao.query(builder.prepare());
			
			mMapManager.displayNetworks(data);
			
			mLocalSource.close();
		}
	};
	
	
	private void openLocalConnection() throws SQLException {
		File dir = new File(SQLITE_DATABASE_DIR);
		dir.mkdirs();
		mLocalSource = new AndroidConnectionSource(SQLiteDatabase.openOrCreateDatabase(SQLITE_DATABASE_DIR + SQLITE_DATABASE_FILE, null));
		mLocalDao = DaoManager.createDao(mLocalSource, WifiNetwork.class);
	}
	
	
	private void openRemoteConnection() throws SQLException {
		String url = "jdbc:mysql://"
				+ MainActivity.mSharedPrefences.getString(SettingsActivity.KEY_PREF_DATABASE_SERVER, "")
				+ "/test_wifi_map";
		mRemoteSource = new JdbcConnectionSource(url, DATABASE_LOGIN, DATABASE_PASS);
		mRemoteDao = DaoManager.createDao(mRemoteSource, WifiNetwork.class);
	}
	
			
	public WifiDatabaseManager(GoogleMapManager mapManager) {
		mMapManager = mapManager;
	}
	
	
	public void queryLocal() {
		mExecutor.execute(mQueryLocal);
	}
	
	
	public void queryRemote() {
		mExecutor.execute(mQueryRemote);
	}
	
	
	public void importRemote() {
		mExecutor.execute(mImportRemote);
	}
	
	
	public void queryRegion(VisibleRegion region) {
		mRegion = region;
		mExecutor.execute(mQueryRegion);
	}
}
