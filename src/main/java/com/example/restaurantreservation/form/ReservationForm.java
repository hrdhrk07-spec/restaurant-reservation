package com.example.restaurantreservation.form;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 予約のフォームクラス
 */
@Data
public class ReservationForm {

    @NotNull
    private LocalDateTime reservedAt;

    @NotNull
    @Min(1)
    private Integer numberOfGuests;

}
