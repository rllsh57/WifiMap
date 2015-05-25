package by.bsuir.osisp.wifimap;

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
	private Executor mExecutor = Executors.newFixedThreadPool(1);

	
	private Runnable mConnectTask = new ExceptionSafeTask() {
		@Override
		protected void task() throws Exception {
			mConnectionSource = new JdbcConnectionSource(DATABASE_URL, DATABASE_LOGIN, DATABASE_PASS);
			mDataAccessObject = DaoManager.createDao(mConnectionSource, WifiNetwork.class);
			MainActivity.makeToast("count = " + mDataAccessObject.queryForAll().size());
		}
	};
	private Runnable mDisconnectTask = new ExceptionSafeTask() {
		@Override
		protected void task() throws Exception {
			mConnectionSource.close();
		}
	};
	
	
	public void connectToDatabase() {
		mExecutor.execute(mConnectTask);
	}
		
	
	public void disconnectFromDatabase() {
		mExecutor.execute(mDisconnectTask);
	}
}
