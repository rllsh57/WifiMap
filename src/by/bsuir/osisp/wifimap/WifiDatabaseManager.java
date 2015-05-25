package by.bsuir.osisp.wifimap;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;


public class WifiDatabaseManager {
	
	private static final String DATABASE_URL = "jdbc:mysql://192.168.100.2:3306/test_wifi_map";
	private static final String DATABASE_LOGIN = "rllsh57";
	private static final String DATABASE_PASS = "107295";
	
	private JdbcConnectionSource mConnectionSource;
	private Dao<WifiNetwork, Integer> mDataAccessObject;
	private Executor mExecutor = Executors.newSingleThreadExecutor();

	
	private Runnable mConnectTask = new ExceptionSafeTask() {
		@Override
		protected void task() throws Exception {
			mConnectionSource = new JdbcConnectionSource(DATABASE_URL, DATABASE_LOGIN, DATABASE_PASS);
			mDataAccessObject = DaoManager.createDao(mConnectionSource, WifiNetwork.class);
		}
	};
	private Runnable mDisconnectTask = new ExceptionSafeTask() {
		@Override
		protected void task() throws Exception {
			mConnectionSource.close();
		}
	};
	private Runnable mQueryTask = new ExceptionSafeTask() {
		@Override
		protected void task() throws Exception {
			List<WifiNetwork> networks = mDataAccessObject.queryForAll();
			MainActivity.displayWifiNetworks(networks);
		}
	};

	
	public void connectToDatabase() {
		mExecutor.execute(mConnectTask);
	}
		
	
	public void disconnectFromDatabase() {
		mExecutor.execute(mDisconnectTask);
	}
	
	
	public void queryWifiNetworks() {
		mExecutor.execute(mQueryTask);
	}
}
