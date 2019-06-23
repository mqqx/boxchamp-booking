package com.hammer.apps.boxchampbooking.util;

public class UrlUtils {
	private static final String APP_URL = "https://boxchamp.io/";

	public static String buildUrl(String urlPath) {
		return APP_URL + urlPath;
	}
}
