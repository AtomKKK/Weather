package com.zbb.weather.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class WeatherService extends Service {
	
	private WeatherServiceBinder binder = new WeatherServiceBinder();
	private String TAG ="WeatherService";
	

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return binder;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.i(TAG, "onCreate");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.i(TAG, "onDestroy");
		super.onDestroy();
	}
	
	public void test() {
		Log.i(TAG, "test");
	}
	
	public class WeatherServiceBinder extends Binder{
		public WeatherService getService() {
			return WeatherService.this;
		}
	}
}
