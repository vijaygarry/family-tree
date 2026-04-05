package com.neasaa.familytree.enums;

public enum FamilyRegistrationStatus {
    PENDING,
    DUPLICATE,
    INVALID,
    PROCESSED;

    public static FamilyRegistrationStatus getStatusByString(String input) {
        for (FamilyRegistrationStatus status : FamilyRegistrationStatus.values()) {
            if (status.name().equalsIgnoreCase(input)) {
                return status;
            }
        }
        return null;
    }
}
