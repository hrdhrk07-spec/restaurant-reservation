package com.example.restaurantreservation.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalTime;
import java.util.List;

/**
 * レストラン情報を管理するエンティティクラス
 */
@Data
@EqualsAndHashCode(callSuper = false)
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
     * 受付開始時刻
     */
    private LocalTime receptionStartTime;

    /**
     * 受付終了時刻
     */
    private LocalTime receptionEndTime;

    /**
     * 定休日
     */
    @OneToMany(mappedBy = "restaurant")
    @ToString.Exclude
    private List<Holiday> holidays;

}
