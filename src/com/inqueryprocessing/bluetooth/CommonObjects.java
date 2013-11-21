package com.inqueryprocessing.bluetooth;

import java.util.TimeZone;

public class CommonObjects {
	public static DataBaseManager database;
	public static boolean reportCreated = false;

	public static String getTimeZone() {
		TimeZone tz = TimeZone.getDefault();
		String str = "" + tz.getOffset(1000);
		int in = (Integer.parseInt(str) / 1000) / 3600;
		return String.valueOf(String.valueOf(in));

	}

	public static boolean isWhiteSpace(String str) {
		if ((str == null) || str.matches("^\\s*$") || str.equals("")) {
			return true;
		} else {
			return false;
		}
	}
}
