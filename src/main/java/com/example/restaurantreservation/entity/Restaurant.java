package com.example.restaurantreservation.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * レストラン情報を管理するエンティティクラス
 */
@Data
@Entity
@Table(name = "restaurants")
public class Restaurant {

    /** ID（主キー） */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** レストラン名 */
    private String name;

    /** ジャンル */
    private String cuisineType;

    /** 所在地 */
    private String location;

    /** 休業日 */
    private String holidays;

    /** 予約開始時刻 */
    private LocalTime openTime;

    /** 予約終了時刻 */
    private LocalTime closeTime;

    /** 作成日時 */
    private LocalDateTime createdAt;

    /** 更新日時 */
    private LocalDateTime updatedAt;
}
