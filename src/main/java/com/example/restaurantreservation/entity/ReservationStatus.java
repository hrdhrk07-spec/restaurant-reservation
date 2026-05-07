package com.example.restaurantreservation.entity;

/**
 * 予約状況のステータス
 */
public enum ReservationStatus {
    PENDING("予約申請中"),
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
