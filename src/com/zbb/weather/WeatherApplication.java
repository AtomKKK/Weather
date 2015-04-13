package com.zbb.weather;

import com.thinkland.sdk.android.SDKInitializer;

import android.app.Application;

public class WeatherApplication extends Application{
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		SDKInitializer.initialize(getApplicationContext());	
	}
}
