package com.example.restaurantreservation.enums;

/**
 * 定休日の曜日
 */
public enum HolidayDayOfWeek {
    SUNDAY("日", 0),
    MONDAY("月", 1),
    TUESDAY("火", 2),
    WEDNESDAY("水", 3),
    THURSDAY("木", 4),
    FRIDAY("金", 5),
    SATURDAY("土", 6);

    private final String label;
    private final int value;


    HolidayDayOfWeek(String label, int value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public int getValue() {
        return value;
    }
}
