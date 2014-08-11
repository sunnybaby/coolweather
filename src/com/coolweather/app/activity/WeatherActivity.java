package com.coolweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import net.youmi.android.banner.AdSize;
import net.youmi.android.banner.AdView;
import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coolweather.app.R;
import com.coolweather.app.adapters.CityWeatherPageAdapter;
import com.coolweather.app.component.CirclePageIndicator;
import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Weather;
import com.coolweather.app.service.AutoUpdateService;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

public class WeatherActivity extends Activity implements OnClickListener,OnPageChangeListener{

	/**
	 * 用于显示城市名
	 */
	private TextView cityNameText;
	/**
	 * 切换城市按钮
	 */
	private Button switchCity;
	/**
	 * 更新天气按钮
	 */
	private Button refreshWeather;
	/**
	 * 用于显示城市名
	 */
	private ViewPager weatherViewPager;
	
	private CityWeatherPageAdapter cityWeatherPageAdapter;
	
	private List<Weather> weathers = new ArrayList<Weather>();
	
	private List<County> selectedCountyList;
	
	private CirclePageIndicator pageIndicator;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		// 初始化各控件
		cityNameText = (TextView) findViewById(R.id.city_name);
		switchCity = (Button) findViewById(R.id.switch_city);
		refreshWeather = (Button) findViewById(R.id.refresh_weather);
		weatherViewPager = (ViewPager) findViewById(R.id.vp_city_weather);
		pageIndicator = (CirclePageIndicator) findViewById(R.id.vpi_indicator);
		
		selectedCountyList = getIntent().getParcelableArrayListExtra("selected");
		cityNameText.setText(selectedCountyList.get(0).getCountyName());
		for (int i = 0; i < selectedCountyList.size(); i++) {
			Weather weather = new Weather();
			weathers.add(weather);
		}
		cityWeatherPageAdapter = new CityWeatherPageAdapter(this, weathers);
		weatherViewPager.setAdapter(cityWeatherPageAdapter);
		pageIndicator.setFillColor(getResources().getColor(
				R.color.vpi__background_holo_light));
		pageIndicator.setRadius(5);// viewpager底部小圆点半径
		pageIndicator.setSnap(true);
		pageIndicator.setViewPager(weatherViewPager, 0);
		
		weatherViewPager.setOnPageChangeListener(this);
		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);
		//实例化广告条
	    AdView adView = new AdView(this, AdSize.FIT_SCREEN);
	    //获取要嵌入广告条的布局
	    LinearLayout adLayout=(LinearLayout)findViewById(R.id.adLayout);
	    //将广告条加入到布局中
	    adLayout.addView(adView);
	    
	    queryWeatherCode(selectedCountyList.get(0).getCountyCode(), 0);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.switch_city:
			Intent intent = new Intent(this, ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh_weather:
//			publishText.setText("同步中...");
//			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//			String weatherCode = prefs.getString("weather_code", "");
			int position = weatherViewPager.getCurrentItem();
			queryWeatherCode(selectedCountyList.get(position).getCountyCode(), position);
			break;
		default:
			break;
		}
	}
	
	/**
	 * 查询县级代号所对应的天气代号。
	 */
	private void queryWeatherCode(String countyCode, int position) {
		String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
		queryFromServer(address, "countyCode", position);
	}

	/**
	 * 查询天气代号所对应的天气。
	 */
	private void queryWeatherInfo(String weatherCode, int position) {
		String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
		queryFromServer(address, "weatherCode", position);
	}
	
	/**
	 * 根据传入的地址和类型去向服务器查询天气代号或者天气信息。
	 */
	private void queryFromServer(final String address, final String type, final int position) {
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			@Override
			public void onFinish(final String response) {
				if ("countyCode".equals(type)) {
					if (!TextUtils.isEmpty(response)) {
						// 从服务器返回的数据中解析出天气代号
						String[] array = response.split("\\|");
						if (array != null && array.length == 2) {
							String weatherCode = array[1];
							queryWeatherInfo(weatherCode, position);
						}
					}
				} else if ("weatherCode".equals(type)) {
					// 处理服务器返回的天气信息
					Weather weather = Utility.handleWeatherResponse(WeatherActivity.this, response);
					weathers.set(position, weather);
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							cityWeatherPageAdapter.updateUIAtPosition(position);
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						/*publishText.setText("同步失败");*/
					}
				});
			}
		});
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int position) {
		cityNameText.setText(selectedCountyList.get(position).getCountyName());
		pageIndicator.setCurrentItem(position);
		queryWeatherCode(selectedCountyList.get(position).getCountyCode(), position);
	}
	
	/**
	 * 从SharedPreferences文件中读取存储的天气信息，并显示到界面上。
	 *//*
	private void showWeather() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		cityNameText.setText( prefs.getString("city_name", ""));
		temp1Text.setText(prefs.getString("temp1", ""));
		temp2Text.setText(prefs.getString("temp2", ""));
		weatherDespText.setText(prefs.getString("weather_desp", ""));
		publishText.setText("今天" + prefs.getString("publish_time", "") + "发布");
		currentDateText.setText(prefs.getString("current_date", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
		Intent intent = new Intent(this, AutoUpdateService.class);
		startService(intent);
	}*/

}