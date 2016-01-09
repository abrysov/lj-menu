package com.fosslabs.android.utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

public class JSTimeWorker {
	private static final String TAG = "JSTimeWorker";

	public static final class SimpleDateConst {
		public static final String DATE = "yyyy-MM-dd";
		public static final String TIME = "HH:mm:ss";
		public static final String DATE_TIME = "yyyy-MM-dd HH:mm:ss";
		public static final String DATE_TIME_UTC = "yyyy-MM-dd HH:mm:ss Z";
		public static final String DATE_T_TIME_SSSZ = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	}

	public static String getSimpleDateFormat(Date date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
		return sdf.format(date);
	}

	public static int[] getTimeArray(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int hours = calendar.get(Calendar.HOUR_OF_DAY);
		int minutes = calendar.get(Calendar.MINUTE);
		int seconds = calendar.get(Calendar.SECOND);
		return new int[] { hours, minutes, seconds };
	}
	
	public static int[] getTimeArray(DateTime date) {
		int hours = date.getHourOfDay();
		int minutes = date.getMinuteOfHour();
		int seconds = date.getSecondOfMinute();
		return new int[] { hours, minutes, seconds };
	}

	public static String getTimeZonePretty(String timezoneId) {

		// String[] ids = TimeZone.getAvailableIDs();
		// TimeZone d = TimeZone.getTimeZone(ids[i]);
		TimeZone d = TimeZone.getTimeZone(timezoneId);
		if (JSLog.isDebug()) {
			JSLog.d(TAG, "time zone." + d.getDisplayName(Locale.getDefault()));
			JSLog.d(TAG, "savings." + d.getDSTSavings());
			JSLog.d(TAG, "offset." + d.getRawOffset());
		}

		if (!timezoneId.matches(".*/.*")) {
			return null;
		}

		String region = timezoneId.replaceAll(".*/", "").replaceAll("_", " ");
		int hours = Math.abs(d.getRawOffset()) / 3600000;
		int minutes = Math.abs(d.getRawOffset() / 60000) % 60;
		String sign = d.getRawOffset() >= 0 ? "+" : "-";

		String timeZonePretty = String.format("%s (UTC %s %02d:%02d)", region,
				sign, hours, minutes);
		if (JSLog.isDebug()) {
			JSLog.d(TAG, "timeZonePretty " + timeZonePretty);
		}
		return timeZonePretty;
	}

	public static Date getDateInTimeZone(Date currentDate, String timeZoneId) {
		TimeZone tz = TimeZone.getTimeZone(timeZoneId);
		Calendar mbCal = new GregorianCalendar(tz);
		mbCal.setTimeInMillis(currentDate.getTime());
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, mbCal.get(Calendar.YEAR));
		cal.set(Calendar.MONTH, mbCal.get(Calendar.MONTH));
		cal.set(Calendar.DAY_OF_MONTH, mbCal.get(Calendar.DAY_OF_MONTH));
		cal.set(Calendar.HOUR_OF_DAY, mbCal.get(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, mbCal.get(Calendar.MINUTE));
		cal.set(Calendar.SECOND, mbCal.get(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, mbCal.get(Calendar.MILLISECOND));

		return cal.getTime();
	}

	public static String getCity(Context context, String townId) {
		if (JSLog.isDebug()) {
			JSLog.d(TAG, "getCity " + townId);
		}
		Locale ruLocale = new Locale("ru", "RU");
		Geocoder geocoder = new Geocoder(context, ruLocale);
		StringBuilder builder = new StringBuilder();
		List<Address> addresses = null;
		int start = townId.indexOf("/");
		String town = townId.substring(++start, townId.length());
		if (JSLog.isDebug()) {
			JSLog.d(TAG, "getCity town " + town);
		}
		Log.d(TAG, "getCity town " + town);
		try {
			addresses = geocoder.getFromLocationName(town, 1);
			if (JSLog.isDebug()) {
				JSLog.d(TAG, "getCity addresses " + addresses);
			}
			
		} catch (IOException e) {
			JSLog.e(TAG, "getCity town " + e.getMessage());
			e.printStackTrace();
		}
		if (addresses != null && addresses.size() > 0) {
			
			Address address = addresses.get(0);
			builder.append(address.getLocality());
		} else {
			builder.append(town);
		}
		return builder.toString();
	}

	public static DateTime timeOfTown(String townId) {
		DateTime dt = new DateTime();
		DateTime dtWithZone = dt.withZone(DateTimeZone.forID(townId));
		return dtWithZone;
	}
}
