package com.neasaa.familytree.enums;

public enum MaritalStatus {
	Single, Married, Divorced, Widowed, Separated, Engaged;
	
	public static MaritalStatus getMaritalStatus (String input) {
		for(MaritalStatus maritalStatus : MaritalStatus.values()) {
			if(maritalStatus.name().equalsIgnoreCase(input)) {
				return maritalStatus;
			}
		}
		return null;
	}
}
