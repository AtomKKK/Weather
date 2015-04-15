package com.zbb.weather.service;

import java.security.PublicKey;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.thinkland.sdk.android.DataCallBack;
import com.thinkland.sdk.android.JuheData;
import com.thinkland.sdk.android.Parameters;
import com.zbb.weather.bean.AQIqualityBean;
import com.zbb.weather.bean.FutureWeatherBean;
import com.zbb.weather.bean.HoursWeatherBean;
import com.zbb.weather.bean.WeatherBean;

import android.R.integer;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class WeatherService extends Service {

	private boolean isRunning = false;
	private int count = 0;

	private AQIqualityBean aqiBean;
	private WeatherBean weatherBean;
	private List<HoursWeatherBean> list;

	private OnParserCallBack callback;

	private WeatherServiceBinder binder = new WeatherServiceBinder();
	private String TAG = "WeatherService";
	private String city;

	private static final int REPEAT_MSG = 0x01;

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == REPEAT_MSG) {

				getCityWeather();
				sendEmptyMessageDelayed(REPEAT_MSG, 30 * 60 * 1000);
			}
		}

	};

	public interface OnParserCallBack {
		public void OnParserComplete(AQIqualityBean aqiBean,
				WeatherBean weatherBean, List<HoursWeatherBean> list);
	}

	public void setCallBack(OnParserCallBack callback) {
		this.callback = callback;
	}

	public void removeCallBack() {
		callback = null;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return binder;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		city = "成都";
		mHandler.sendEmptyMessage(REPEAT_MSG);
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

	public class WeatherServiceBinder extends Binder {
		public WeatherService getService() {
			return WeatherService.this;
		}
	}

	private AQIqualityBean parserAQIquality(String result) {
		AQIqualityBean qBean = null;
		try {
			JSONObject aQIqualityJson = new JSONObject(result);
			if (aQIqualityJson.getInt("resultcode") == 200
					&& aQIqualityJson.getInt("error_code") == 0) {

				JSONArray aqiJsonArray = aQIqualityJson.getJSONArray("result");
				JSONObject qJsonObject = aqiJsonArray.getJSONObject(0);
				qBean = new AQIqualityBean();
				qBean.setPM_Aquality(qJsonObject.getString("PM2.5"));
				qBean.setPM_index(qJsonObject.getString("quality"));
				return qBean;
			} else {

				Toast.makeText(getApplicationContext(), "PM2.5 ERROR",
						Toast.LENGTH_SHORT).show();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return qBean;
	}

	private WeatherBean parserWeather(String result) {

		WeatherBean bean = null;

		JSONObject weatherJsonObject;
		try {
			weatherJsonObject = new JSONObject(result);

			// weatherJsonObject=JSONObject.fromObject(result);

			Log.i("weatherJsonObject", weatherJsonObject.toString());
			int code = weatherJsonObject.getInt("resultcode");
			int error_code = weatherJsonObject.getInt("error_code");
			if (error_code == 0 && code == 200) {
				JSONObject resulJson = weatherJsonObject
						.getJSONObject("result");
				bean = new WeatherBean();
				// today
				JSONObject todayJsonObject = resulJson.getJSONObject("today");
				bean.setCity(todayJsonObject.getString("city"));
				bean.setTemp(todayJsonObject.getString("temperature"));
				bean.setWeather_str(todayJsonObject.getString("weather"));
				bean.setDressing_index(todayJsonObject
						.getString("dressing_index"));
				bean.setUv_index(todayJsonObject.getString("uv_index"));
				bean.setWeather_id(todayJsonObject.getJSONObject("weather_id")
						.getString("fa"));

				// sk
				JSONObject skJsonObject = resulJson.getJSONObject("sk");
				bean.setNow_temp(skJsonObject.getString("temp"));
				bean.setWind(skJsonObject.getString("wind_direction")
						+ skJsonObject.getString("wind_strength"));
				bean.setHumidity(skJsonObject.getString("humidity"));
				bean.setReleaseString(skJsonObject.getString("time"));

				// future
				JSONArray futureArray = resulJson.getJSONArray("future");
				List<FutureWeatherBean> futureList = new ArrayList<FutureWeatherBean>();
				Log.i("futureArray.length():", futureArray.length() + "");

				SimpleDateFormat sdf = new SimpleDateFormat("yyyymmdd");
				Date date = new Date(System.currentTimeMillis());
				Calendar calendar = Calendar.getInstance();
				int month = calendar.get(Calendar.MONTH) + 1;
				String currentDate;
				if (month < 10) {
					currentDate = calendar.get(Calendar.YEAR) + "0" + month
							+ "" + calendar.get(Calendar.DAY_OF_MONTH);
				} else {
					currentDate = calendar.get(Calendar.YEAR) + "" + month + ""
							+ calendar.get(Calendar.DAY_OF_MONTH);
				}

				for (int i = 0; i < futureArray.length(); i++) {

					JSONObject furtureJson = futureArray.getJSONObject(i);
					FutureWeatherBean futureBean = new FutureWeatherBean();

					/*
					 * Date dateF = sdf.parse(furtureJson.getString("date"));
					 * String dataS = sdf.format(date);
					 */

					Log.i("furtureJson.getString(\"date\"))",
							furtureJson.getString("date"));
					Log.i("Today is ", "");
					if (currentDate.equals(furtureJson.getString("date"))) {
						Log.i("parserWeather continue :", "continue");
						continue;
					}

					/*
					 * if (!dateF.after(date)) {
					 * Log.i("parserWeather continue :", "continue"); continue;
					 * }
					 */

					futureBean.setTemp(furtureJson.getString("temperature"));
					futureBean.setWeek(furtureJson.getString("week"));
					futureBean.setWeather_id(furtureJson.getJSONObject(
							"weather_id").getString("fa"));
					futureList.add(futureBean);
					if (futureList.size() == 3) {
						break;
					}

				}
				Log.i("parserWeather futureList Size :", futureList.size() + "");
				bean.setFutureList(futureList);
			} else {
				Toast.makeText(getApplicationContext(), "WEATHER_ERROR",
						Toast.LENGTH_SHORT).show();
			}

		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return bean;
	}

	private List<HoursWeatherBean> parserWeather3h(String result) {
		JSONObject hourJsonObject;
		List<HoursWeatherBean> hourWeatherList = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		Date date = new Date();
		try {
			hourJsonObject = new JSONObject(result);
			int code = hourJsonObject.getInt("resultcode");
			int error_code = hourJsonObject.getInt("error_code");
			if (error_code == 0 && code == 200) {
				hourWeatherList = new ArrayList<HoursWeatherBean>();
				JSONArray hourArray = hourJsonObject.getJSONArray("result");
				for (int i = 0; i < hourArray.length(); i++) {
					JSONObject hourJson = hourArray.getJSONObject(i);

					Date hDate = sdf.parse(hourJson.getString("sfdate"));
					if (!hDate.after(date)) {
						continue;
					}
					HoursWeatherBean hoursWeatherBean = new HoursWeatherBean();
					hoursWeatherBean.setWeather_id(hourJson
							.getString("weatherid"));
					hoursWeatherBean.setTemp1(hourJson.getString("temp1"));
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(hDate);
					hoursWeatherBean.setTime(calendar.get(Calendar.HOUR_OF_DAY)
							+ "");
					hourWeatherList.add(hoursWeatherBean);
					if (hourWeatherList.size() == 5) {
						break;
					}
				}
			} else {
				Toast.makeText(getApplicationContext(), "WEATHER_ERROR",
						Toast.LENGTH_SHORT).show();
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hourWeatherList;
	}

	public void getCityWeather() {

		if (isRunning) {
			return;
		}

		isRunning = true;
		count = 0;

		Parameters params = new Parameters();
		params.add("cityname", city);
		// params.add("dtype", "json");
		params.add("key", "eeec9d46381c7a1e65982820ee96cfc0");
		params.add("format", 2);
		/**
		 * 请求的方法 参数: 第一个参数 接口id 第二个参数 接口请求的url 第三个参数 接口请求的方式 第四个参数
		 * 接口请求的参数,键值对com.thinkland.sdk.android.Parameters类型; 第五个参数
		 * 请求的回调方法,com.thinkland.sdk.android.DataCallBack;
		 * 
		 */
		JuheData.executeWithAPI(1, "http://v.juhe.cn/weather/index",
				JuheData.GET, params, new DataCallBack() {

					/**
					 * @param err
					 *            错误码,0为成功
					 * @param reason
					 *            原因
					 * @param result
					 *            数据
					 */
					@Override
					public void resultLoaded(int err, String reason,
							String result) {
						// TODO Auto-generated method stub
						if (err == 0) {

							Log.i("err", err + "");
							Log.i("reason", reason);
							Log.i("result", result);
							synchronized (this) {
								count++;

							}
							weatherBean = parserWeather(result);
							if (weatherBean != null) {

								// setWeatherViews(bean);

							}
							if (count == 3) {

								if (callback != null) {
									callback.OnParserComplete(aqiBean,
											weatherBean, list);
								}
								isRunning = false;

							}
						} else {
							Toast.makeText(getApplicationContext(), reason,
									Toast.LENGTH_SHORT).show();
						}
					}

				});

		Parameters params3h = new Parameters();
		params3h.add("cityname", city);
		params3h.add("dtype", "json");
		params3h.add("key", "eeec9d46381c7a1e65982820ee96cfc0");

		JuheData.executeWithAPI(1, "http://v.juhe.cn/weather/forecast3h",
				JuheData.GET, params3h, new DataCallBack() {

					@Override
					public void resultLoaded(int err, String reason,
							String result) {
						// TODO Auto-generated method stub
						if (err == 0) {

							Log.i("err", err + "");
							Log.i("reason", reason);
							Log.i("result", result);
							list = parserWeather3h(result);
							if (list != null) {
								// setHourViews(list);
							}
							synchronized (this) {
								count++;

							}
							if (count == 3) {

								if (callback != null) {
									callback.OnParserComplete(aqiBean,
											weatherBean, list);
								}
								isRunning = false;

							}

						} else {
							Toast.makeText(getApplicationContext(), reason,
									Toast.LENGTH_SHORT).show();
						}
					}

				});

		Parameters paramsAQI = new Parameters();
		paramsAQI.add("city", city);
		paramsAQI.add("dtype", "json");
		paramsAQI.add("key", "d2a1c383f3da1e678d85e6b076c8e0f2");

		JuheData.executeWithAPI(33,
				"http://web.juhe.cn:8080/environment/air/pm", JuheData.GET,
				paramsAQI, new DataCallBack() {

					@Override
					public void resultLoaded(int err, String reason,
							String result) {
						// TODO Auto-generated method stub
						if (err == 0) {

							Log.i("err", err + "");
							Log.i("reason", reason);
							Log.i("result", result);
							aqiBean = parserAQIquality(result);
							if (aqiBean != null) {
								// setAQIView(qBean);
							}
							synchronized (this) {
								count++;

							}
							if (count == 3) {

								if (callback != null) {
									callback.OnParserComplete(aqiBean,
											weatherBean, list);
								}

								isRunning = false;

							}

						} else {
							Toast.makeText(getApplicationContext(), reason,
									Toast.LENGTH_SHORT).show();
						}
					}

				});

		Log.i(TAG, "count = " + count);

	}
}
