package com.neasaa.familytree.enums;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Getter
public enum IndianState {
    ANDHRA_PRADESH("AP"),
    ARUNACHAL_PRADESH("AR"),
    ASSAM("AS"),
    BIHAR("BR"),
    CHHATTISGARH("CG"),
    GOA("GA"),
    GUJARAT("GJ"),
    HARYANA("HR"),
    HIMACHAL_PRADESH("HP"),
    JHARKHAND("JH"),
    KARNATAKA("KA"),
    KERALA("KL"),
    MADHYA_PRADESH("MP"),
    MAHARASHTRA("MH"),
    MANIPUR("MN"),
    MEGHALAYA("ML"),
    MIZORAM("MZ"),
    NAGALAND("NL"),
    ODISHA("OD"),
    PUNJAB("PB"),
    RAJASTHAN("RJ"),
    SIKKIM("SK"),
    TAMIL_NADU("TN"),
    TELANGANA("TG"),
    TRIPURA("TR"),
    UTTARAKHAND("UK"),
    UTTAR_PRADESH("UP"),
    WEST_BENGAL("WB");

    private final String shortName;

    IndianState(String shortName) {
        this.shortName = shortName;
    }

    public static IndianState fromShortName(String shortName) {
        for (IndianState state : values()) {
            if (state.getShortName().equalsIgnoreCase(shortName)) {
                return state;
            }
        }
        throw new IllegalArgumentException("No IndianState found for short name: " + shortName);
    }

    public static String getShortStateName (String stateName) {
        for (IndianState state : values()) {
            if (state.name().replace("_", " ").equalsIgnoreCase(stateName)) {
                return state.shortName;
            }
        }
        log.info("Short name not found for state: {}", stateName);
        return stateName;
    }

}
