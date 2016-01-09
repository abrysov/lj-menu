package com.fosslabs.android.utils;

import java.util.Formatter;
import android.util.Log;

public class JSLog {
	private static final boolean DEBUG = false;

	public static boolean isDebug() {
		return DEBUG;
	}

	/**
	 * A trick to reuse a formatter in the same thread
	 */
	private static class ReusableFormatter {

		private Formatter formatter;
		private StringBuilder builder;

		public ReusableFormatter() {
			builder = new StringBuilder();
			formatter = new Formatter(builder);
		}

		public String format(String msg, Object... args) {
			formatter.format(msg, args);
			String s = builder.toString();
			builder.setLength(0);
			return s;
		}
	}

	private static final ThreadLocal<ReusableFormatter> thread_local_formatter = new ThreadLocal<ReusableFormatter>() {
		protected ReusableFormatter initialValue() {
			return new ReusableFormatter();
		}
	};

	public static String format(String msg, Object... args) {
		ReusableFormatter formatter = thread_local_formatter.get();
		return formatter.format(msg, args);
	}

	public static void d(String TAG, String msg) {
		if (DEBUG)
			Log.d(TAG, msg);
	}

	public static void d(String TAG, String msg, Object... args) {
		if (DEBUG)
			Log.d(TAG, format(msg, args));
	}

	public static void d(String TAG, Throwable err, String msg, Object... args) {
		if (DEBUG)
			Log.d(TAG, format(msg, args), err);
	}

	public static void i(String TAG, String msg) {
		if (DEBUG)
			Log.i(TAG, msg);
	}

	public static void i(String TAG, String msg, Object... args) {
		if (DEBUG)
			Log.i(TAG, format(msg, args));
	}

	public static void i(String TAG, Throwable err, String msg, Object... args) {
		if (DEBUG)
			Log.i(TAG, format(msg, args), err);
	}

	public static void w(String TAG, String msg) {
		if (DEBUG)
			Log.w(TAG, msg);
	}

	public static void w(String TAG, String msg, Object... args) {
		if (DEBUG)
			Log.w(TAG, format(msg, args));
	}

	public static void w(String TAG, Throwable err, String msg, Object... args) {
		if (DEBUG)
			Log.w(TAG, format(msg, args), err);
	}

	public static void e(String TAG, String msg) {
		if (DEBUG)
			Log.e(TAG, msg);
	}

	public static void e(String TAG, String msg, Object... args) {
		if (DEBUG)
			Log.e(TAG, format(msg, args));
	}

	public static void e(String TAG, Throwable err, String msg, Object... args) {
		if (DEBUG)
			Log.e(TAG, format(msg, args), err);
	}
}
