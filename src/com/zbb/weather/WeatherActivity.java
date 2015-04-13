package com.zbb.weather;

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
import com.zbb.weather.service.WeatherService;
import com.zbb.weather.service.WeatherService.WeatherServiceBinder;
import com.zbb.weather.swiperefresh.PullToRefreshBase;
import com.zbb.weather.swiperefresh.PullToRefreshBase.OnRefreshListener;
import com.zbb.weather.swiperefresh.PullToRefreshScrollView;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class WeatherActivity extends Activity {

	private PullToRefreshScrollView mPullToRefreshScrollView;
	private ScrollView scrollView;

	private TextView tv_city, tv_release, tv_now_weather, tv_today_tmp,
			tv_now_tmp, tv_aqi, tv_qua, tv_next_three, tv_next_six,
			tv_next_nine, tv_next_twelve, tv_next_fifteen, tv_next_three_tmp,
			tv_next_six_tmp, tv_next_nine_tmp, tv_next_twelve_tmp,
			tv_next_fifteen_tmp, tv_today, tv_today_tmp_a, tv_today_tmp_b,
			tv_tomorrow, tv_tomorrow_tmp_a, tv_tomorrow_tmp_b, tv_third,
			tv_third_tmp_a, tv_third_tmp_b, tv_fourth, tv_fourth_tmp_a,
			tv_fourth_tmp_b, tv_felt_aire_temp, tv_wet, tv_wind, tv_uv_index,
			tv_dressing_index;

	private ImageView iv_now_weather, iv_next_three_weather,
			iv_next_six_weather, iv_next_nine_weather, iv_next_twelve_weather,
			iv_next_fifteen_weather, iv_today_tmp, iv_tomorrow_tmp,
			iv_third_tmp, iv_fourth_tmp;

	private boolean isRunning = false;
	private int count = 0;
	
	private Context mContext;
	private WeatherService mService;
	
	private String TAG ="WeatherActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather);
		mContext = WeatherActivity.this	;
		//init();
		initService();
		/*
		 * getCityWeather();
		 * 
		 * getCityWeather3h();
		 * 
		 * getAQIquality();
		 */
		
	}
	
	private void initService() {
		
		Log.i(TAG, "initService");
		Intent intent = new Intent(mContext,WeatherService.class);
		startService(intent);
		bindService(intent, conn, Context.BIND_AUTO_CREATE);
	}
	
	ServiceConnection conn = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			Log.i(TAG, "onServiceConnected");
			mService = ((WeatherServiceBinder)service).getService();
			mService.test();
		}
	};

	private void getCityWeather() {

		if (isRunning) {
			return;
		}

		isRunning = true;
		count = 0;

		Parameters params = new Parameters();
		params.add("cityname", "成都");
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
					WeatherBean bean = null;

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
							count++;
							bean = parserWeather(result);
							if (bean != null) {

								setWeatherViews(bean);

							}
							if (count == 3) {

								mPullToRefreshScrollView.onRefreshComplete();
								isRunning = true;

							}
						} else {
							Toast.makeText(getApplicationContext(), reason,
									Toast.LENGTH_SHORT).show();
						}
					}

				});

		Parameters params3h = new Parameters();
		params3h.add("cityname", "成都");
		params3h.add("dtype", "json");
		params3h.add("key", "eeec9d46381c7a1e65982820ee96cfc0");

		JuheData.executeWithAPI(1, "http://v.juhe.cn/weather/forecast3h",
				JuheData.GET, params3h, new DataCallBack() {
					List<HoursWeatherBean> list = null;

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
								setHourViews(list);
							}
							count++;
							if (count == 3) {

								mPullToRefreshScrollView.onRefreshComplete();
								isRunning = true;

							}

						} else {
							Toast.makeText(getApplicationContext(), reason,
									Toast.LENGTH_SHORT).show();
						}
					}

				});

		Parameters paramsAQI = new Parameters();
		paramsAQI.add("city", "成都");
		paramsAQI.add("dtype", "json");
		paramsAQI.add("key", "d2a1c383f3da1e678d85e6b076c8e0f2");

		JuheData.executeWithAPI(33,
				"http://web.juhe.cn:8080/environment/air/pm", JuheData.GET,
				paramsAQI, new DataCallBack() {
					AQIqualityBean qBean = null;

					@Override
					public void resultLoaded(int err, String reason,
							String result) {
						// TODO Auto-generated method stub
						if (err == 0) {

							Log.i("err", err + "");
							Log.i("reason", reason);
							Log.i("result", result);
							qBean = parserAQIquality(result);
							if (qBean != null) {
								setAQIView(qBean);
							}
							count++;
							if (count == 3) {

								mPullToRefreshScrollView.onRefreshComplete();
								isRunning = true;

							}

						} else {
							Toast.makeText(getApplicationContext(), reason,
									Toast.LENGTH_SHORT).show();
						}
					}

				});
		
		Log.i(TAG,"count = "+count);
		
		/*if (count == 3) {

			mPullToRefreshScrollView.onRefreshComplete();
			isRunning = true;

		}*/

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

	private void setAQIView(AQIqualityBean qBean) {

		tv_aqi.setText(qBean.getPM_index());
		tv_qua.setText(qBean.getPM_Aquality());

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

	private void setHourViews(List<HoursWeatherBean> hourBeanList) {
		if (hourBeanList != null && hourBeanList.size() == 5) {

			set3HourViews(tv_next_three, iv_next_three_weather,
					tv_next_three_tmp, hourBeanList.get(0));
			set3HourViews(tv_next_six, iv_next_six_weather, tv_next_six_tmp,
					hourBeanList.get(1));
			set3HourViews(tv_next_nine, iv_next_nine_weather, tv_next_nine_tmp,
					hourBeanList.get(2));
			set3HourViews(tv_next_twelve, iv_next_twelve_weather,
					tv_next_twelve_tmp, hourBeanList.get(3));
			set3HourViews(tv_next_fifteen, iv_next_fifteen_weather,
					tv_next_fifteen_tmp, hourBeanList.get(4));

		}

	}

	private void set3HourViews(TextView tv_time, ImageView iv, TextView tv_tmp,
			HoursWeatherBean hourBean) {
		String prefixString = null;
		int time = Integer.valueOf(hourBean.getTime());
		if (time >= 6 && time < 18) {

			prefixString = "d";

		} else {
			prefixString = "n";
		}
		tv_time.setText(hourBean.getTime() + ":00");
		iv.setImageResource(getResources().getIdentifier(
				prefixString + hourBean.getWeather_id(), "drawable",
				"com.zbb.weather"));
		tv_tmp.setText(hourBean.getTemp1() + "°");
	}

	private void setWeatherViews(WeatherBean bean) {
		tv_release.setText(bean.getReleaseString());
		Log.i("tv_release", tv_release.getText() + "");
		tv_now_tmp.setText(bean.getNow_temp());
		tv_today_tmp.setText(bean.getTemp());
		tv_city.setText(bean.getCity());
		tv_wet.setText(bean.getHumidity());
		tv_wind.setText(bean.getWind());
		tv_uv_index.setText(bean.getUv_index());
		tv_dressing_index.setText(bean.getDressing_index());
		tv_now_weather.setText(bean.getWeather_str());
		// iv_now_weather.setImageResource(getResources().getIdentifier("",
		// defType, defPackage))

		String[] temArr = bean.getTemp().split("~");
		String temp_str_a = temArr[0].substring(0, temArr[0].indexOf("℃"));
		String temp_str_b = temArr[1].substring(0, temArr[1].indexOf("℃"));
		tv_today_tmp_a.setText(temp_str_a + "°");
		tv_today_tmp_b.setText(temp_str_b + "°");

		iv_today_tmp.setImageResource(getResources().getIdentifier(
				"d" + bean.getWeather_id(), "drawable", "com.zbb.weather"));

		List<FutureWeatherBean> futureList = bean.getFutureList();
		if (futureList != null && futureList.size() == 3) {

			Log.i("setViews futureList Size :", futureList.size() + "");
			setFurtureWeatherViews(tv_tomorrow, iv_tomorrow_tmp,
					tv_tomorrow_tmp_a, tv_tomorrow_tmp_b, futureList.get(0));
			setFurtureWeatherViews(tv_third, iv_third_tmp, tv_third_tmp_a,
					tv_third_tmp_b, futureList.get(1));
			setFurtureWeatherViews(tv_fourth, iv_fourth_tmp, tv_fourth_tmp_a,
					tv_fourth_tmp_b, futureList.get(2));

		}
	}

	private void setFurtureWeatherViews(TextView tv_week, ImageView iv_weather,
			TextView tv_temp_a, TextView tv_temp_b, FutureWeatherBean bean) {

		tv_week.setText(bean.getWeek());
		iv_weather.setImageResource(getResources().getIdentifier(
				"d" + bean.getWeather_id(), "drawable", "com.zbb.weather"));
		String[] temArr = bean.getTemp().split("~");
		String temp_str_a = temArr[0].substring(0, temArr[0].indexOf("℃"));
		String temp_str_b = temArr[1].substring(0, temArr[1].indexOf("℃"));
		tv_temp_a.setText(temp_str_a + "°");
		tv_temp_b.setText(temp_str_b + "°");
	}

	private void init() {

		mPullToRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.pull_refresh_scrollview);
		mPullToRefreshScrollView
				.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

					@Override
					public void onRefresh(
							PullToRefreshBase<ScrollView> refreshView) {
						// TODO Auto-generated method stub
						getCityWeather();
					}
				});

		scrollView = mPullToRefreshScrollView.getRefreshableView();

		tv_city = (TextView) findViewById(R.id.tv_city);
		tv_release = (TextView) findViewById(R.id.tv_release);
		tv_now_weather = (TextView) findViewById(R.id.tv_now_weather);
		tv_today_tmp = (TextView) findViewById(R.id.tv_today_tmp);
		tv_now_tmp = (TextView) findViewById(R.id.tv_now_tmp);
		tv_aqi = (TextView) findViewById(R.id.tv_aqi);
		tv_qua = (TextView) findViewById(R.id.tv_qua);
		tv_next_three = (TextView) findViewById(R.id.tv_next_three);
		tv_next_six = (TextView) findViewById(R.id.tv_next_six);
		tv_next_nine = (TextView) findViewById(R.id.tv_next_nine);
		tv_next_twelve = (TextView) findViewById(R.id.tv_next_twelve);
		tv_next_fifteen = (TextView) findViewById(R.id.tv_next_fifteen);
		tv_next_three_tmp = (TextView) findViewById(R.id.tv_next_three_tmp);
		tv_next_six_tmp = (TextView) findViewById(R.id.tv_next_six_tmp);
		tv_next_nine_tmp = (TextView) findViewById(R.id.tv_next_nine_tmp);
		tv_next_twelve_tmp = (TextView) findViewById(R.id.tv_next_twelve_tmp);
		tv_next_fifteen_tmp = (TextView) findViewById(R.id.tv_next_fifteen_tmp);
		tv_today = (TextView) findViewById(R.id.tv_today);
		tv_today_tmp_a = (TextView) findViewById(R.id.tv_today_tmp_a);
		tv_today_tmp_b = (TextView) findViewById(R.id.tv_today_tmp_b);
		tv_tomorrow = (TextView) findViewById(R.id.tv_tomorrow);
		tv_tomorrow_tmp_a = (TextView) findViewById(R.id.tv_tomorrow_tmp_a);
		tv_tomorrow_tmp_b = (TextView) findViewById(R.id.tv_tomorrow_tmp_b);
		tv_third = (TextView) findViewById(R.id.tv_third);
		tv_third_tmp_a = (TextView) findViewById(R.id.tv_third_tmp_a);
		tv_third_tmp_b = (TextView) findViewById(R.id.tv_third_tmp_b);
		tv_fourth = (TextView) findViewById(R.id.tv_fourth);
		tv_fourth_tmp_a = (TextView) findViewById(R.id.tv_fourth_tmp_a);
		tv_fourth_tmp_b = (TextView) findViewById(R.id.tv_fourth_tmp_b);
		tv_felt_aire_temp = (TextView) findViewById(R.id.tv_felt_aire_temp);
		tv_wet = (TextView) findViewById(R.id.tv_wet);
		tv_wind = (TextView) findViewById(R.id.tv_wind);
		tv_uv_index = (TextView) findViewById(R.id.tv_uv_index);
		tv_dressing_index = (TextView) findViewById(R.id.tv_dressing_index);

		iv_now_weather = (ImageView) findViewById(R.id.iv_now_weather);
		iv_next_three_weather = (ImageView) findViewById(R.id.iv_next_three_weather);
		iv_next_six_weather = (ImageView) findViewById(R.id.iv_next_six_weather);
		iv_next_nine_weather = (ImageView) findViewById(R.id.iv_next_nine_weather);
		iv_next_twelve_weather = (ImageView) findViewById(R.id.iv_next_twelve_weather);
		iv_next_fifteen_weather = (ImageView) findViewById(R.id.iv_next_fifteen_weather);
		iv_today_tmp = (ImageView) findViewById(R.id.iv_today_tmp);
		iv_tomorrow_tmp = (ImageView) findViewById(R.id.iv_tomorrow_tmp);
		iv_third_tmp = (ImageView) findViewById(R.id.iv_third_tmp);
		iv_fourth_tmp = (ImageView) findViewById(R.id.iv_fourth_tmp);

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unbindService(conn);
	}
	
}