package com.example.restaurantreservation.controller;

import com.example.restaurantreservation.entity.Holiday;
import com.example.restaurantreservation.entity.Restaurant;
import com.example.restaurantreservation.entity.SeatDetail;
import com.example.restaurantreservation.enums.HolidayDayOfWeek;
import com.example.restaurantreservation.form.ReservationForm;
import com.example.restaurantreservation.service.ReservationService;
import com.example.restaurantreservation.service.RestaurantService;
import com.example.restaurantreservation.service.SeatDetailService;
import com.example.restaurantreservation.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationController.class)
@WithMockUser(roles = "USER")
public class ReservationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    private ReservationService reservationService;

    @MockitoBean
    private RestaurantService restaurantService;

    @MockitoBean
    private SeatDetailService seatDetailService;

    @MockitoBean
    private UserService userService;

    private Restaurant restaurant;
    private List<SeatDetail> seatDetails;

    private SeatDetail setSeatDetail(Long id, int numberOfSeats, int personPerSeat, int duration) {
        SeatDetail seatDetail = new SeatDetail();
        seatDetail.setId(id);
        seatDetail.setNumberOfSeats(numberOfSeats);
        seatDetail.setPersonPerSeat(personPerSeat);
        seatDetail.setRestaurant(restaurant);
        seatDetail.setDuration(duration);
        return seatDetail;
    }

    @BeforeEach
    public void init() {

        // レストランの設定
        restaurant = new Restaurant();
        restaurant.setId(1000L);
        restaurant.setName("テストレストラン");
        restaurant.setReceptionStartTime(LocalTime.of(19, 0));
        restaurant.setReceptionEndTime(LocalTime.of(22, 0));

        // 定休日の設定
        Holiday holiday1 = new Holiday();
        holiday1.setHolidayDayOfWeek(HolidayDayOfWeek.SATURDAY);
        holiday1.setRestaurant(restaurant);

        Holiday holiday2 = new Holiday();
        holiday2.setHolidayDayOfWeek(HolidayDayOfWeek.SUNDAY);
        holiday2.setRestaurant(restaurant);

        restaurant.setHolidays(new ArrayList<Holiday>(Arrays.asList(holiday1, holiday2)));

        // 席詳細の設定
        seatDetails = new ArrayList<SeatDetail>();
        seatDetails.add(setSeatDetail(1001L, 1, 2, 60));
        seatDetails.add(setSeatDetail(1002L, 2, 4, 120));

    }

    @Test
    @DisplayName("予約入力画面(GET)")
    void input() throws Exception {

        // モックの設定
        when(restaurantService.getRestaurantById(any())).thenReturn(restaurant);

        // mockMvc.perform
        mockMvc.perform(MockMvcRequestBuilders.get("/reservation-input/1000"))
                .andExpectAll(
                        status().isOk(),
                        view().name("user/reservation-input"),
                        model().attribute("restaurant", restaurant),
                        model().attribute("reservationForm", new ReservationForm())
                );

    }

    @Test
    @WithMockUser
    @DisplayName("予約入力_挙動_空席確認ボタン_画面遷移")
    void reservationInput_seatAvailability_screenTransition() throws Exception {

        // モックの設定
        when(restaurantService.getRestaurantById(any())).thenReturn(restaurant);
        when(reservationService.isHoliday(any(), any())).thenReturn(false);
        when(reservationService.getAvailableSeats(any(),any(),anyInt())).thenReturn(new ArrayList<>());

        // 予約日時の設定
        LocalDate date = LocalDate.now(ZoneId.of("Asia/Tokyo")).plusDays(1L);
        while (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            date = date.plusDays(1L);
        }
        LocalDateTime time = LocalDateTime.of(date, LocalTime.of(20, 0));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        String reservedAt = time.format(formatter);

        // mockMvc.perform
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/reservation-input/1000")
                        .sessionAttr("reservationForm", new ReservationForm())
                        .with(csrf())
                        .param("reservedAt", reservedAt)
                        .param("numberOfGuests", "2")
                )
                .andExpectAll(
                        status().isOk(),
                        view().name("user/reservation-input"),
                        model().attributeExists("restaurant"),
                        model().attributeExists("seatDetailList"),
                        model().attributeExists("reservationForm")
                );

    }

}
