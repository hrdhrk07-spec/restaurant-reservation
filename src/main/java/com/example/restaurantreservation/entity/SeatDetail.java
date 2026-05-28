package com.example.restaurantreservation.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 席詳細情報を管理するエンティティクラス
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "seat_details")
public class SeatDetail extends BaseEntity {

    /**
     * 席詳細ID（主キー）
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * レストランID（外部キー）
     */
    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    /**
     * 一席あたりの人数（例：二人席なら2が入る）
     */
    private int personPerSeat;

    /**
     * 席セット数（例：二人席が5テーブルなら、5が入る）
     */
    private int numberOfSeats;

    /**
     * 所要時間（分）
     */
    private int duration;

}
