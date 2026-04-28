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
public class Restaurant extends BaseEntity {

    /**
     * ID（主キー）
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * レストラン名
     */
    @Column(nullable = false)
    private String name;

    /**
     * ジャンル
     */
    @Column(nullable = false)
    private String cuisineType;

    /**
     * 所在地
     */
    private String location;

    /**
     * 画像パス
     */
    private String imagePath;

    /**
     * 休業日
     */
    private String holidays;

    /**
     * 受付開始時刻
     */
    private LocalTime receptionStartTime;

    /**
     * 受付終了時刻
     */
    private LocalTime receptionEndTime;

}
