package com.example.restaurantreservation.enums;

/**
 * 予約状況のステータス
 */
public enum ReservationStatus {
    CONFIRMED("予約確定"),
    CANCELLED("キャンセル済み");

    private final String label;

    ReservationStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

}
