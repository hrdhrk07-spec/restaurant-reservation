package com.example.restaurantreservation.form;

import com.example.restaurantreservation.enums.HolidayDayOfWeek;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;
import java.util.List;

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

    private String imagePath;

    private List<HolidayDayOfWeek> holidayDayOfWeeks;

    @NotNull
    private LocalTime receptionStartTime;

    @NotNull
    private LocalTime receptionEndTime;

    @Valid
    private SeatDetailForm seatDetail = new SeatDetailForm();

}
