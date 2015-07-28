package by.bsuir.osisp.wifimap;

import android.util.Log;


public abstract class ExceptionSafeTask implements Runnable {

	@Override
	public void run() {
		try {
			task();
		}
		catch (Exception ex) {
			Log.e(this.getClass().getName(), ex.toString());
			MainActivity.makeToast(ex.toString());
		}
	}
	
	protected abstract void task() throws Exception;
}
