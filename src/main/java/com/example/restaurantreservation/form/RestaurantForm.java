package com.example.restaurantreservation.form;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;

/**
 * レストラン登録のフォームクラス
 */
@Data
public class RestaurantForm {

    @NotBlank
    private String name;

    @NotBlank
    private String cuisineType;

    @NotBlank
    private String location;

    @NotBlank
    private String imagePath;


    private String holidays;

    @NotNull
    private LocalTime receptionStartTime;

    @NotNull
    private LocalTime receptionEndTime;

    @Valid
    private SeatDetailForm seatDetail = new SeatDetailForm();

}
