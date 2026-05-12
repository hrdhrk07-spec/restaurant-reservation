package com.example.restaurantreservation.enums;

import java.time.DayOfWeek;
import java.util.Arrays;

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

    /**
     * DayOfWeek → HolidayDayOfWeek変換
     *
     * @return HolidayDayOfWeek型の曜日
     */
    public static HolidayDayOfWeek of(DayOfWeek dayOfWeek) {
        return Arrays.stream(values())
                .filter(h -> h.value == dayOfWeek.getValue() % 7)
                .findFirst()
                .orElseThrow();
    }

}
