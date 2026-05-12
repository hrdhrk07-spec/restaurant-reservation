package com.example.restaurantreservation.form;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 予約のフォームクラス
 */
@Data
public class ReservationForm {

    @NotNull
    @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm")
    private LocalDateTime reservedAt;

    @NotNull
    @Min(1)
    private Integer numberOfGuests;

}
