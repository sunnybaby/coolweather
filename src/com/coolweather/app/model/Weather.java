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
	private String temp1;
	/**
	 * 最低温度
	 */
	private String temp2;
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
	
	public String getTemp1() {
		return temp1;
	}
	
	public void setTemp1(String temp1) {
		this.temp1 = temp1;
	}
	
	public String getTemp2() {
		return temp2;
	}
	
	public void setTemp2(String temp2) {
		this.temp2 = temp2;
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
