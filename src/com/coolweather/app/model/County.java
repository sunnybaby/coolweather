package com.coolweather.app.model;

import android.os.Parcel;
import android.os.Parcelable;

public class County implements Parcelable{

	private int id;

	private String countyName;

	private String countyCode;

	private int cityId;
	
	private int isSelected;
	
	public static final Parcelable.Creator<County> CREATOR = new Creator<County>() {

		@Override
		public County createFromParcel(Parcel source) {
			County county = new County();
			county.setId(source.readInt());
			county.setCountyName(source.readString());
			county.setCountyCode(source.readString());
			county.setCityId(source.readInt());
			county.setIsSelected(source.readInt());
			return county;
		}

		@Override
		public County[] newArray(int size) {
			// TODO Auto-generated method stub
			return new County[size];
		}
	};

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCountyName() {
		return countyName;
	}

	public void setCountyName(String countyName) {
		this.countyName = countyName;
	}

	public String getCountyCode() {
		return countyCode;
	}

	public void setCountyCode(String countyCode) {
		this.countyCode = countyCode;
	}

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public int getIsSelected() {
		return isSelected;
	}

	public void setIsSelected(int isSelected) {
		this.isSelected = isSelected;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(getId());
		dest.writeString(getCountyName());
		dest.writeString(getCountyCode());
		dest.writeInt(getCityId());
		dest.writeInt(getIsSelected());
	}
	
}
