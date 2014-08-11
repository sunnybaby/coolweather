package com.coolweather.app.model;

/**
 * <dl>
 * <dt>Weather.java</dt>
 * <dd>Description:天气信息实体类</dd>
 * <dd>CreateDate: 2014-8-11 上午10:10:38</dd>
 * </dl>
 * 
 * @author WX
 */
public class Weather {
	
	/**
	 * 地名
	 */
	private String areaName;
	/**
	 * 天气代码
	 */
	private String weatherCode;
	/**
	 * 最高温度
	 */
	private String tempHigh;
	/**
	 * 最低温度
	 */
	private String tempLow;
	/**
	 * 天气描述
	 */
	private String weatherDesp;
	/**
	 * 发布时间
	 */
	private String publishTime;
	
	public String getAreaName() {
		return areaName;
	}
	
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
	
	public String getWeatherCode() {
		return weatherCode;
	}
	
	public void setWeatherCode(String weatherCode) {
		this.weatherCode = weatherCode;
	}
	
	public String getTempHigh() {
		return tempHigh;
	}
	
	public void setTempHigh(String tempHigh) {
		this.tempHigh = tempHigh;
	}
	
	public String getTempLow() {
		return tempLow;
	}
	
	public void setTempLow(String tempLow) {
		this.tempLow = tempLow;
	}
	
	public String getWeatherDesp() {
		return weatherDesp;
	}
	
	public void setWeatherDesp(String weatherDesp) {
		this.weatherDesp = weatherDesp;
	}
	
	public String getPublishTime() {
		return publishTime;
	}
	
	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
	}

}
