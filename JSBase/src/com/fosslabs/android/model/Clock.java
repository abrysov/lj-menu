package com.fosslabs.android.model;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.joda.time.DateTime;

import android.content.Context;

import com.fosslabs.android.utils.JSTimeWorker;

public class Clock {
	// TODO: this mapping are temprorary fixes for performance downgrades
	// Should think about stable solution.
	private static final Map<String, String> TOWN_MAPPING = new HashMap<String, String>();
	private static final Map<String, String> TIME_ZONE_MAPPING = new HashMap<String, String>();
	
	private String mTownId;
	private boolean mByTownId;
	private int mTimeDifference;
	public Clock (boolean byTownId, String townId){
		mTownId = townId;
		mByTownId = byTownId;
	}
	
	public DateTime getDateTime(){
//		Calendar calendar = new GregorianCalendar();
//		
//		calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + mTimeDifference);
		
//		DateTime dt = new DateTime(
//				calendar.get(Calendar.YEAR),
//				calendar.get(Calendar.MONTH),
//				calendar.get(Calendar.DAY_OF_MONTH),
//				calendar.get(Calendar.HOUR_OF_DAY),
//				calendar.get(Calendar.MINUTE),
//				calendar.get(Calendar.SECOND));
		//return JSTimeWorker.timeOfTown(mTownId);
		
		DateTime dt = new DateTime();
		dt = DateTime.now().plusHours(mTimeDifference);
		return dt;
	}
	
	public DateTime getDateTime(DateTime dtNow){
		return dtNow.plusHours(mTimeDifference);
	}
	
	private String mTZId = "";
	private String mCity = "Москва";
	private String mTZSummer = "Etc/GMT+3:00";
	private String mTZWinter = "Etc/GMT+4:00";
	
	public Clock(String tzId, String city, String tzSummer, String tzWinter){
		mTZId = tzId;
		mCity = city;
		mTZSummer = tzSummer;
		mTZWinter = tzWinter;
	}
		
	public Clock(String tzId){
		mTZId = tzId;
		mCity = null;
		mTZSummer = null;
		mTZWinter = null;
	}
	
	public Clock(String townId, String city, int timeDifference){
		mTownId = townId;
		mCity = city;
		mTimeDifference = timeDifference;
	}

	public String getTime(){
		Date date = new Date();
		date =JSTimeWorker.getDateInTimeZone(date, mTZId);
		return JSTimeWorker.getSimpleDateFormat(date,
				JSTimeWorker.SimpleDateConst.TIME);
	}
	
	public Date getDate(){
		Date date = new Date();
		date =JSTimeWorker.getDateInTimeZone(date, mTZId);
		return date;
	}
	
	
	public String getTZId() {
		return mTZId;
	}

	public void setTZId(String tZId) {
		mTZId = tZId;
	}

	public String getCity(Context context) {
//		if(mByTownId){
//			
//			String result = TOWN_MAPPING.get(mTownId);
//			if (null == result) {
//				result = JSTimeWorker.getCity(context, mTownId);
//				TOWN_MAPPING.put(mTownId, result);
//			}
//			
//			return result;
//		}
//		if(mCity == null ){
//			
//			String result = TIME_ZONE_MAPPING.get(mTZId);
//			if (null == result) {
//				result = JSTimeWorker.getTimeZonePretty(mTZId);
//				TIME_ZONE_MAPPING.put(mTZId, result);
//			}
//			
//			return result;
//		}
		return mCity;
	}

	public void setCity(String city) {
		mCity = city;
	}

	public String getTZSummer() {
		return mTZSummer;
	}

	public void setTZSummer(String tZSummer) {
		mTZSummer = tZSummer;
	}

	public String getTZWinter() {
		return mTZWinter;
	}

	public void setTZWinter(String tZWinter) {
		mTZWinter = tZWinter;
	}
	
	
}
