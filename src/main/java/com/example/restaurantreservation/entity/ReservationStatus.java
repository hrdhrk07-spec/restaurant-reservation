package com.example.restaurantreservation.entity;

/**
 * 予約状況のステータス
 */
public enum ReservationStatus {
    PENDING,    // 予約申請中
    CONFIRMED,  // 予約確定
    CANCELLED   // キャンセル済み
}
