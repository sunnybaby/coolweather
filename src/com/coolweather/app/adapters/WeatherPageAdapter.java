package com.coolweather.app.adapters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.coolweather.app.R;
import com.coolweather.app.model.Weather;

import android.R.integer;
import android.content.Context;
import android.provider.Telephony.Mms.Addr;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherPageAdapter extends PagerAdapter {
	
	private Context mContext;
	/**
	 * 用于保存已选地区的天气信息
	 */
	private List<Weather> mWeathers;
	/**
	 * 用于保存ViewPager各页的视图
	 */
	private List<View> mPages = new ArrayList<View>();
	
	public WeatherPageAdapter(Context context, List<Weather> weathers) {
		mContext = context;
		mWeathers = weathers;
		initPages();
	}
	
	/** 
	 * @author: WX
	 * @Title: initPages 
	 * @Description: 根据传入的天气信息 ，初始化 ViewPager
	 * @date: 2014-8-11 上午10:03:50
	 */
	private void initPages() {
		if (mWeathers != null) {
			for (int i = 0; i < mWeathers.size(); i++) {
				View currentPageView = LayoutInflater.from(mContext).inflate(R.layout.city_weather_page_layout, null);
				LinearLayout weatherInfoLayout = (LinearLayout) currentPageView.findViewById(R.id.weather_info_layout);
				TextView publishText = (TextView) currentPageView.findViewById(R.id.publish_text);
				TextView weatherDespText = (TextView) currentPageView.findViewById(R.id.weather_desp);
				TextView temp1Text = (TextView) currentPageView.findViewById(R.id.temp1);
				TextView temp2Text = (TextView) currentPageView.findViewById(R.id.temp2);
				TextView currentDateText = (TextView) currentPageView.findViewById(R.id.current_date);
				
				Weather w = mWeathers.get(i);
				temp1Text.setText(TextUtils.isEmpty(w.getTemp1()) ? "--" : w.getTemp1());
				temp2Text.setText(TextUtils.isEmpty(w.getTemp2()) ? "--" : w.getTemp2());
				weatherDespText.setText(TextUtils.isEmpty(w.getWeatherDesp()) ? "--" : w.getWeatherDesp());
				publishText.setText(getPublishText(w.getPublishTime()));
				currentDateText.setText(TextUtils.isEmpty(w.getPublishTime()) ? "--年--月--日" : w.getPublishTime().split("-")[0]);
				weatherInfoLayout.setVisibility(View.VISIBLE);
				
				currentPageView.setTag(i);
				mPages.add(currentPageView);
			}
		}
	}
	
	public void addPage(View view) {
		mPages.add(view);
		notifyDataSetChanged();
	}

	public void updateUIAtPosition(int position) {
		View currentPageView = mPages.get(position);
		TextView publishText = (TextView) currentPageView.findViewById(R.id.publish_text);
		TextView weatherDespText = (TextView) currentPageView.findViewById(R.id.weather_desp);
		TextView temp1Text = (TextView) currentPageView.findViewById(R.id.temp1);
		TextView temp2Text = (TextView) currentPageView.findViewById(R.id.temp2);
		TextView currentDateText = (TextView) currentPageView.findViewById(R.id.current_date);
		
		temp1Text.setText(mWeathers.get(position).getTemp1());
		temp2Text.setText(mWeathers.get(position).getTemp2());
		weatherDespText.setText(mWeathers.get(position).getWeatherDesp());
		publishText.setText("今天" + mWeathers.get(position).getPublishTime().split("-")[1] + "发布");
		currentDateText.setText(mWeathers.get(position).getPublishTime().split("-")[0]);
		
		notifyDataSetChanged();
	}
	
	private String getPublishText(String savedPublishTime) {
		String savedPublishDate = "";
		if (!TextUtils.isEmpty(savedPublishTime)) {
			savedPublishDate = savedPublishTime.split("-")[0];
			try {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
				Calendar savedCalendar = Calendar.getInstance();
				savedCalendar.setTime(dateFormat.parse(savedPublishDate));
				Calendar currentCalendar = Calendar.getInstance();
				if (savedCalendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR)
						&& savedCalendar.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH)) {
					int duration = currentCalendar.get(Calendar.DATE);
					switch (duration) {
					case 0:
						return "今天" + savedPublishTime.split("-")[1] + "发布";
					case 1:
						return "昨天" + savedPublishTime.split("-")[1] + "发布";
					case 2:
						return "前天" + savedPublishTime.split("-")[1] + "发布";
					default:
						return "3天前发布";
					}
				} else {
					return "3天前发布";
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "--";
			}
		} else {
			return "--";
		}
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mPages.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == arg1;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		// TODO Auto-generated method stub
		container.addView(mPages.get(position), 0);
		return mPages.get(position);
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
		container.removeView((View) object);
	}
}
