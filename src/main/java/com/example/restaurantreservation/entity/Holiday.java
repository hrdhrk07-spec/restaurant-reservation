package com.example.restaurantreservation.entity;

import com.example.restaurantreservation.enums.HolidayDayOfWeek;
import jakarta.persistence.*;
import lombok.Data;

/**
 * 定休日情報を管理するエンティティクラス
 */
@Data
@Entity
@Table(name = "holidays")
public class Holiday extends BaseEntity {

    /**
     * 休日ID（主キー）
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
     * 曜日
     */
    @Enumerated(EnumType.STRING)
    private HolidayDayOfWeek holidayDayOfWeek;

}
