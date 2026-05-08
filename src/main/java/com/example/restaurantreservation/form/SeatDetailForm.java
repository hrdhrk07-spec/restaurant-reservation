package com.example.restaurantreservation.form;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * レストラン登録のフォームクラス
 */
@Data
public class SeatDetailForm {

    @NotNull
    @Min(1)
    private Integer personPerSeat;

    @NotNull
    @Min(1)
    private Integer numberOfSeats;

    @NotNull
    @Min(1)
    private Integer duration;

}
