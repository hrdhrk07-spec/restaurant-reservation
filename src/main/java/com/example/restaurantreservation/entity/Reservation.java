package com.example.restaurantreservation.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 予約情報を管理するエンティティクラス
 */
@Data
@Entity
@Table(name = "reservations")
public class Reservation {

    /** 予約ID（主キー） */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** ユーザID（外部キー） */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /** レストランID（外部キー） */
    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    /** 予約した席詳細（外部キー） */
    @ManyToOne
    @JoinColumn(name = "seat_detail_id")
    private SeatDetail seatDetail;

    /** 予約日時 */
    private LocalDateTime reservedAt;

    /** 予約人数 */
    private int numberOfGuests;

    /** 予約ステータス（例：PENDING / CONFIRMED / CANCELLED） */
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    /** 作成日時 */
    private LocalDateTime createdAt;

    /** 更新日時 */
    private LocalDateTime updatedAt;
}
