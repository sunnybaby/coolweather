package com.coolweather.app.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.youmi.android.AdManager;
import android.R.integer;
import android.app.Activity;
import android.app.DownloadManager.Query;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.app.R;
import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

public class ChooseAreaActivity extends Activity {

	public static final int LEVEL_SELECTED = 0;
	public static final int LEVEL_PROVINCE = 1;
	public static final int LEVEL_CITY = 2;
	public static final int LEVEL_COUNTY = 3;
	
	private ProgressDialog progressDialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private CoolWeatherDB coolWeatherDB;
	private List<String> dataList = new ArrayList<String>();
	/**
	 * 省列表
	 */
	private List<Province> provinceList;
	/**
	 * 市列表
	 */
	private List<City> cityList;
	/**
	 * 县列表
	 */
	private List<County> countyList;
	/**
	 * 选中的省份
	 */
	private Province selectedProvince;
	/**
	 * 选中的城市
	 */
	private City selectedCity;
	/**
	 * 当前选中的级别
	 */
	private int currentLevel;
	/**
	 * 是否从WeatherActivity中跳转过来。
	 */
	private boolean isFromWeatherActivity;
	/**
	 * 已选城市列表
	 */
	private List<County> selectedCountyList;
	/**
	 * 当前定位城市
	 */
	private Location mCurrentLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AdManager.getInstance(this).init("cf9c2a749cd97145","289874826c698edd", false);
		coolWeatherDB = CoolWeatherDB.getInstance(this);
		isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
		selectedCountyList = coolWeatherDB.getAllSelectedCounty();
		if (selectedCountyList != null && selectedCountyList.size() > 0 && !isFromWeatherActivity) {
			startWeatherAcitvity(0);
			finish();
			return;
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		listView = (ListView) findViewById(R.id.list_view);
		registerForContextMenu(listView);
		titleText = (TextView) findViewById(R.id.title_text);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index,
					long arg3) {
				if (currentLevel == LEVEL_SELECTED) {
					if (index == dataList.size() - 1) {
						queryProvinces();
					} else if (index == 0){
						
					} else {
						startWeatherAcitvity(index - 1);
						finish();
					}
				} else if (currentLevel == LEVEL_PROVINCE) {
					selectedProvince = provinceList.get(index);
					queryCities();
				} else if (currentLevel == LEVEL_CITY) {
					selectedCity = cityList.get(index);
					queryCounties();
				} else if (currentLevel == LEVEL_COUNTY) {
					County county = countyList.get(index);
					county.setIsSelected(1);
					coolWeatherDB.updateCounty(county);
					int position = -1;
					if (selectedCountyList == null) {
						position = 0;
					} else {
						position = selectedCountyList.size();
					}
					startWeatherAcitvity(position);
					finish();
				}
			}
		});
		if (selectedCountyList != null && selectedCountyList.size() > 0) {
			showSelectedCounties();
		} else {
			queryProvinces();  // 加载省级数据
		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		int id = item.getItemId();
		int position = ((AdapterContextMenuInfo)(item.getMenuInfo())).position;
		switch (id) {
		case R.id.action_delete:
			if (position != selectedCountyList.size() - 1) {
				coolWeatherDB.deleteSelectCountyByCode(selectedCountyList.get(position - 1).getCountyCode());
				selectedCountyList.clear();
				selectedCountyList = coolWeatherDB.getAllSelectedCounty();
				showSelectedCounties();
			}
			break;
		default:
			break;
		}
		return super.onContextItemSelected(item);
	}

	/**
	 * 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询。
	 */
	private void queryProvinces() {
		provinceList = coolWeatherDB.loadProvinces();
		if (provinceList.size() > 0) {
			dataList.clear();
			for (Province province : provinceList) {
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("中国");
			currentLevel = LEVEL_PROVINCE;
		} else {
			queryFromServer(null, "province");
		}
	}

	/**
	 * 查询选中省内所有的市，优先从数据库查询，如果没有查询到再去服务器上查询。
	 */
	private void queryCities() {
		cityList = coolWeatherDB.loadCities(selectedProvince.getId());
		if (cityList.size() > 0) {
			dataList.clear();
			for (City city : cityList) {
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		} else {
			queryFromServer(selectedProvince.getProvinceCode(), "city");
		}
	}
	
	/**
	 * 查询选中市内所有的县，优先从数据库查询，如果没有查询到再去服务器上查询。
	 */
	private void queryCounties() {
		countyList = coolWeatherDB.loadCounties(selectedCity.getId());
		if (countyList.size() > 0) {
			dataList.clear();
			for (County county : countyList) {
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTY;
		} else {
			queryFromServer(selectedCity.getCityCode(), "county");
		}
	}
	
	/**
	 * 根据传入的代号和类型从服务器上查询省市县数据。
	 */
	private void queryFromServer(final String code, final String type) {
		String address;
		if (!TextUtils.isEmpty(code)) {
			address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
		} else {
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			@Override
			public void onFinish(String response) {
				boolean result = false;
				if ("province".equals(type)) {
					result = Utility.handleProvincesResponse(coolWeatherDB,
							response);
				} else if ("city".equals(type)) {
					result = Utility.handleCitiesResponse(coolWeatherDB,
							response, selectedProvince.getId());
				} else if ("county".equals(type)) {
					result = Utility.handleCountiesResponse(coolWeatherDB,
							response, selectedCity.getId());
				}
				if (result) {
					// 通过runOnUiThread()方法回到主线程处理逻辑
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							closeProgressDialog();
							if ("province".equals(type)) {
								queryProvinces();
							} else if ("city".equals(type)) {
								queryCities();
							} else if ("county".equals(type)) {
								queryCounties();
							}
						}
					});
				}
			}

			@Override
			public void onError(Exception e) {
				// 通过runOnUiThread()方法回到主线程处理逻辑
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this,
										"加载失败", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}
	
	private void showSelectedCounties() {
		dataList.clear();
		dataList.add("当前定位城市：" + getLocatedCounty());
		for (County county : selectedCountyList) {
			dataList.add(county.getCountyName());
		}
		dataList.add("+ 添加更多城市...");
		adapter.notifyDataSetChanged();
		listView.setSelection(0);
		titleText.setText("已选城市");
		currentLevel = LEVEL_SELECTED;
	}
	
	/**
	 * 显示进度对话框
	 */
	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	
	/**
	 * 关闭进度对话框
	 */
	private void closeProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}
	
	/**
	 * 捕获Back按键，根据当前的级别来判断，此时应该返回市列表、省列表、还是直接退出。
	 */
	@Override
	public void onBackPressed() {
		if (currentLevel == LEVEL_COUNTY) {
			queryCities();
		} else if (currentLevel == LEVEL_CITY) {
			queryProvinces();
		} else if (currentLevel == LEVEL_PROVINCE && selectedCountyList != null && selectedCountyList.size() > 0) {
			showSelectedCounties();
		} else {
			if (isFromWeatherActivity) {
				startWeatherAcitvity(0);
			}
			finish();
		}
	}

	private void startWeatherAcitvity(int position) {
		selectedCountyList = coolWeatherDB.getAllSelectedCounty();
		Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
		intent.putParcelableArrayListExtra("selected", (ArrayList<? extends Parcelable>) selectedCountyList);
		intent.putExtra("position", position);
		startActivity(intent);
	}
	
	private String getLocatedCounty() {
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		Criteria criteria = new Criteria();
		// 获得最好的定位效果
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(false);
		// 使用省电模式
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		// 获得当前的位置提供者
		String provider = locationManager.getBestProvider(criteria, true);
		// 获得当前的位置
		mCurrentLocation = locationManager.getLastKnownLocation(provider);
		while (mCurrentLocation == null) {
			locationManager.requestLocationUpdates(provider, 0, 0, new LocationListener() {
				
				@Override
				public void onStatusChanged(String provider, int status, Bundle extras) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onProviderEnabled(String provider) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onProviderDisabled(String provider) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onLocationChanged(Location location) {
					mCurrentLocation = location;
				}
			});
		}
		Geocoder gc = new Geocoder(this);
		List<Address> addresses = null;
		try {
			addresses = gc.getFromLocation(mCurrentLocation.getLatitude(),
					mCurrentLocation.getLongitude(), 1);
			return addresses.get(0).getAdminArea() + " " + addresses.get(0).getLocality();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}