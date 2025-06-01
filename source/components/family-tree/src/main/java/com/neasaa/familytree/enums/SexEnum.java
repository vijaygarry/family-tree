package com.neasaa.familytree.enums;

public enum SexEnum {
	Male, Female;
	
	public static SexEnum getSexByString (String input) {
		for(SexEnum sex : SexEnum.values()) {
			if(sex.name().equalsIgnoreCase(input)) {
				return sex;
			}
		}
		return null;
	}
}
