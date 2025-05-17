package com.neasaa.base.app.utils;

import com.neasaa.base.app.operation.exception.ValidationException;
import com.neasaa.util.StringUtils;

public class ValidationUtils {
	public static void checkValuePresent (String aValue, String fieldName) {
		if(StringUtils.isEmpty(aValue)) {
			throw new ValidationException ("Required field " + fieldName + " is not provided");
		}
	}
	
	public static void checkObjectPresent (Object aValue, String fieldName) {
		if(aValue == null) {
			throw new ValidationException ("Required field " + fieldName + " is not provided");
		}
	}
	
	public static final void addToDoLog (String todoText, String className) {
		System.out.println("**** TODO: " + todoText + " in class: " + className);
	}
}
