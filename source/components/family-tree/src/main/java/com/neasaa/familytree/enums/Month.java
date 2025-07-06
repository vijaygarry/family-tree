package com.neasaa.familytree.enums;

import lombok.Getter;

@Getter
public enum Month {
    JANUARY("January", "Jan", (short) 1),
    FEBRUARY("February", "Feb", (short)2),
    MARCH("March", "Mar", (short)3),
    APRIL("April", "Apr", (short)4),
    MAY("May", "May", (short)5),
    JUNE("June", "Jun", (short)6),
    JULY("July", "Jul", (short)7),
    AUGUST("August", "Aug", (short)8),
    SEPTEMBER("September", "Sep", (short)9),
    OCTOBER("October", "Oct", (short)10),
    NOVEMBER("November", "Nov", (short)11),
    DECEMBER("December", "Dec", (short)12);


    private final String monthName;
    private final String shortMonthName;
    private final short monthNumber;

    Month(String monthName, String shortMonthName, short monthNumber) {
        this.monthName = monthName;
        this.shortMonthName = shortMonthName;
        this.monthNumber = monthNumber;
    }

    public static Month fromName(String monthName) {
        for (Month month : Month.values()) {
            if (month.shortMonthName.equalsIgnoreCase(monthName) || month.monthName.equalsIgnoreCase(monthName)) {
                return month;
            }
        }
        return null;
    }
    public static Month fromNumber(int monthNumber) {
        for (Month month : Month.values()) {
            if (month.monthNumber == monthNumber) {
                return month;
            }
        }
        return null;
    }

}
