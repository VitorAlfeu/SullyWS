package com.ws.sully.api.utils;

public class FunctionUtils {
	
	public FunctionUtils(){		
	}
	
	public static boolean isNotNullOrEmpty(Object o) {
		return ((o != null) && (!o.toString().isEmpty()));
	}
	
}