package com.coolweather.app.db;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;
import com.coolweather.app.model.Weather;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

public class CoolWeatherDB {

	/**
	 * 数据库名
	 */
	public static final String DB_NAME = "cool_weather";

	/**
	 * 数据库版本
	 */
	public static final int VERSION = 3;

	private static CoolWeatherDB coolWeatherDB;

	private SQLiteDatabase db;

	/**
	 * 将构造方法私有化
	 */
	private CoolWeatherDB(Context context) {
		CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context,
				DB_NAME, null, VERSION);
		db = dbHelper.getWritableDatabase();
	}

	/**
	 * 获取CoolWeatherDB的实例。
	 */
	public synchronized static CoolWeatherDB getInstance(Context context) {
		if (coolWeatherDB == null) {
			coolWeatherDB = new CoolWeatherDB(context);
		}
		return coolWeatherDB;
	}

	/**
	 * 将Province实例存储到数据库。
	 */
	public void saveProvince(Province province) {
		if (province != null) {
			ContentValues values = new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			db.insert("Province", null, values);
		}
	}

	/**
	 * 从数据库读取全国所有的省份信息。
	 */
	public List<Province> loadProvinces() {
		List<Province> list = new ArrayList<Province>();
		Cursor cursor = db
				.query("Province", null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor
						.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor
						.getColumnIndex("province_code")));
				list.add(province);
			} while (cursor.moveToNext());
		}
		return list;
	}

	/**
	 * 将City实例存储到数据库。
	 */
	public void saveCity(City city) {
		if (city != null) {
			ContentValues values = new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id", city.getProvinceId());
			db.insert("City", null, values);
		}
	}

	/**
	 * 从数据库读取某省下所有的城市信息。
	 */
	public List<City> loadCities(int provinceId) {
		List<City> list = new ArrayList<City>();
		Cursor cursor = db.query("City", null, "province_id = ?",
				new String[] { String.valueOf(provinceId) }, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor
						.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor
						.getColumnIndex("city_code")));
				city.setProvinceId(provinceId);
				list.add(city);
			} while (cursor.moveToNext());
		}
		return list;
	}

	/**
	 * 将County实例存储到数据库。
	 */
	public void saveCounty(County county) {
		if (county != null) {
			ContentValues values = new ContentValues();
			values.put("county_name", county.getCountyName());
			values.put("county_code", county.getCountyCode());
			values.put("city_id", county.getCityId());
			db.insert("County", null, values);
		}
	}

	/**
	 * 从数据库读取某城市下所有的县信息。
	 */
	public List<County> loadCounties(int cityId) {
		List<County> list = new ArrayList<County>();
		Cursor cursor = db.query("County", null, "city_id = ?",
				new String[] { String.valueOf(cityId) }, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				County county = new County();
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountyName(cursor.getString(cursor
						.getColumnIndex("county_name")));
				county.setCountyCode(cursor.getString(cursor
						.getColumnIndex("county_code")));
				county.setCityId(cityId);
				county.setIsSelected(cursor.getInt(cursor
						.getColumnIndex("is_selected")));
				county.setWeatherCode(cursor.getString(cursor
						.getColumnIndex("weather_code")));
				list.add(county);
			} while (cursor.moveToNext());
		}
		return list;
	}
	
	/**
	 * 将County实例存储到数据库。
	 */
	public void updateCounty(County county) {
		if (county != null) {
			ContentValues values = new ContentValues();
			values.put("county_name", county.getCountyName());
			values.put("county_code", county.getCountyCode());
			values.put("city_id", county.getCityId());
			values.put("is_selected", county.getIsSelected());
			values.put("weather_code", county.getWeatherCode());
			db.update("County", values, "county_code = ?", new String[] { county.getCountyCode() });
		}
	}
	
	/**
	 * 根据天气代码查询天气信息
	 */
	public County queryCountyByCountyCode(String countyCode) {
		County county = null;
		if (!TextUtils.isEmpty(countyCode)) {
			Cursor cursor = db.query("County", null, "county_code = ?",
					new String[] { countyCode }, null, null, null);
			if (cursor.moveToFirst()) {
				county = new County();
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountyName(cursor.getString(cursor
						.getColumnIndex("county_name")));
				county.setCountyCode(cursor.getString(cursor
						.getColumnIndex("county_code")));
				county.setCityId(cursor.getInt(cursor
						.getColumnIndex("city_id")));
				county.setIsSelected(cursor.getInt(cursor
						.getColumnIndex("is_selected")));
				county.setWeatherCode(cursor.getString(cursor
						.getColumnIndex("weather_code")));
			}
			if (cursor != null) {
				cursor.close();
			}
		}
		return county;
	}

	
	public List<County> getAllSelectedCounty() {
		List<County> list = new ArrayList<County>();
		Cursor cursor = db.query("County", null, "is_selected = ?",
				new String[] { String.valueOf(1) }, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				County county = new County();
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountyName(cursor.getString(cursor
						.getColumnIndex("county_name")));
				county.setCountyCode(cursor.getString(cursor
						.getColumnIndex("county_code")));
				county.setCityId(cursor.getInt(cursor
						.getColumnIndex("city_id")));
				county.setIsSelected(cursor.getInt(cursor
						.getColumnIndex("is_selected")));
				county.setWeatherCode(cursor.getString(cursor
						.getColumnIndex("weather_code")));
				list.add(county);
			} while (cursor.moveToNext());
		}
		return list;
	}
	
	/**
	 * 将Weather实例存储到数据库。
	 */
	public void saveWeather(Weather weather) {
		if (weather != null) {
			ContentValues values = new ContentValues();
			values.put("area_name", weather.getAreaName());
			values.put("weather_code", weather.getWeatherCode());
			values.put("temp1", weather.getTemp1());
			values.put("temp2", weather.getTemp2());
			values.put("weather_desp", weather.getWeatherDesp());
			values.put("publish_time", weather.getPublishTime());
			db.insert("Weather", null, values);
		}
	}
	
	/**
	 * 根据天气代码查询天气信息
	 */
	public Weather queryWeatherInfo(String weatherCode) {
		Weather weather = null;
		if (!TextUtils.isEmpty(weatherCode)) {
			Cursor cursor = db.query("Weather", null, "weather_code = ?",
					new String[] { weatherCode }, null, null, null);
			if (cursor.moveToFirst()) {
				weather = new Weather();
				weather.setAreaName(cursor.getString(cursor.getColumnIndex("area_name")));
				weather.setWeatherCode(cursor.getString(cursor.getColumnIndex("weather_code")));
				weather.setTemp1(cursor.getString(cursor.getColumnIndex("temp1")));
				weather.setTemp2(cursor.getString(cursor.getColumnIndex("temp2")));
				weather.setWeatherDesp(cursor.getString(cursor.getColumnIndex("weather_desp")));
				weather.setPublishTime(cursor.getString(cursor.getColumnIndex("publish_time")));
			}
			if (cursor != null) {
				cursor.close();
			}
		}
		return weather;
	}

	/**
	 * 将Weather实例存储到数据库。
	 */
	public int updateWeather(Weather weather) {
		if (weather != null) {
			ContentValues values = new ContentValues();
			values.put("area_name", weather.getAreaName());
			values.put("weather_code", weather.getWeatherCode());
			values.put("temp1", weather.getTemp1());
			values.put("temp2", weather.getTemp2());
			values.put("weather_desp", weather.getWeatherDesp());
			values.put("publish_time", weather.getPublishTime());
			return db.update("Weather", values, "weather_code = ?", new String[] { weather.getWeatherCode() });
		}
		return -1;
	}
	
	/** 
	 * @author: WX
	 * @Title: deleteSelectCountyByCode 
	 * @Description: 根据城市代码逻辑删除已选城市
	 * @param countyCode
	 * @return 
	 * @date: 2014-9-17 下午4:06:34
	 */
	public int deleteSelectCountyByCode(String countyCode) {
		if (!TextUtils.isEmpty(countyCode)) {
			ContentValues values = new ContentValues();
			values.put("is_selected", "0");
			return db.update("County", values, "county_code = ?", new String[] {countyCode});
		}
		return -1;
	}
	
}