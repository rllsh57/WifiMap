package by.bsuir.osisp.wifimap;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.AndroidConnectionSource;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;


public class WifiDatabaseManager {
	
	private static final String DATABASE_LOGIN = "rllsh57";
	private static final String DATABASE_PASS = "107295";
	
	private ConnectionSource mLocalSource;
	private ConnectionSource mRemoteSource;
	private Dao<WifiNetwork, Integer> mRemoteDao;
	private Dao<WifiNetwork, Integer> mLocalDao;
	private Executor mExecutor = Executors.newSingleThreadExecutor();
	private GoogleMapManager mMapManager;
	
	private Runnable mQueryLocal = new ExceptionSafeTask() {		
		@Override
		protected void task() throws Exception {
			mLocalSource = new AndroidConnectionSource(SQLiteDatabase.openOrCreateDatabase("/data/data/by.bsuir.osisp.wifimap/databases/wifi_map.db", null));
			mLocalDao = DaoManager.createDao(mLocalSource, WifiNetwork.class);
			
			List<WifiNetwork> networks = mLocalDao.queryForAll();
			mMapManager.displayNetworks(networks);
			
			mLocalSource.close();
			
		}
	};
	
	private Runnable mQueryRemote = new ExceptionSafeTask() {		
		@Override
		protected void task() throws Exception {
			String url = "jdbc:mysql://"
					+ "192.168.100.3:3306"
					+ "/test_wifi_map";
			mRemoteSource = new JdbcConnectionSource(url, DATABASE_LOGIN, DATABASE_PASS);
			mRemoteDao = DaoManager.createDao(mRemoteSource, WifiNetwork.class);
			
			List<WifiNetwork> networks = mRemoteDao.queryForAll();
			mMapManager.displayNetworks(networks);
		
			mRemoteSource.close();
		}
	};
	
	private Runnable mImportRemote = new ExceptionSafeTask() {
		@Override
		protected void task() throws Exception {
			mLocalSource = new AndroidConnectionSource(SQLiteDatabase.openOrCreateDatabase("/data/data/by.bsuir.osisp.wifimap/databases/wifi_map.db", null));
			mLocalDao = DaoManager.createDao(mLocalSource, WifiNetwork.class);
			
			String url = "jdbc:mysql://"
					+ "192.168.100.3:3306"
					+ "/test_wifi_map";
			mRemoteSource = new JdbcConnectionSource(url, DATABASE_LOGIN, DATABASE_PASS);
			mRemoteDao = DaoManager.createDao(mRemoteSource, WifiNetwork.class);
			
			try {
				TableUtils.createTable(mLocalSource, WifiNetwork.class);
			}
			catch (SQLException e) {
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
}
