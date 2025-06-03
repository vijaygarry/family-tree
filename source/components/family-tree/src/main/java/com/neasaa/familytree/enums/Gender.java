package com.neasaa.familytree.enums;

public enum Gender {
	Male, Female;
	
	public static Gender getGenderByString (String input) {
		for(Gender gender : Gender.values()) {
			if(gender.name().equalsIgnoreCase(input)) {
				return gender;
			}
		}
		return null;
	}
}
