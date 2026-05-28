package com.example.restaurantreservation.controller;

import com.example.restaurantreservation.entity.Holiday;
import com.example.restaurantreservation.entity.Restaurant;
import com.example.restaurantreservation.entity.SeatDetail;
import com.example.restaurantreservation.enums.HolidayDayOfWeek;
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

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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

    private SeatDetail setSeatDetail(Long id, int numberOfSeats, int personPerSeat, int duration){
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
        restaurant.setReceptionStartTime(LocalTime.of(19,0));
        restaurant.setReceptionEndTime(LocalTime.of(22,0));

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
        seatDetails.add(setSeatDetail(1001L,1,2,60));
        seatDetails.add(setSeatDetail(1002L,2,4,120));

    }

    @Test
    @DisplayName("予約入力_画面表示_レストラン名")
    void reservationInput_displayRestaurantName() throws Exception {

        // モックの設定
        when(restaurantService.getRestaurantById(any())).thenReturn(restaurant);

        // mockMvc.perform
        mockMvc.perform(MockMvcRequestBuilders.get("/reservation-input/1000"))
                .andExpect(MockMvcResultMatchers.content().string(containsString("テストレストラン")));

    }

    @Test
    @DisplayName("予約入力_画面表示_予約受付時間")
    void reservationInput_displayReceptionTime() throws Exception {

        // モックの設定
        when(restaurantService.getRestaurantById(any())).thenReturn(restaurant);

        // mockMvc.perform
        mockMvc.perform(MockMvcRequestBuilders.get("/reservation-input/1000"))
                .andExpect(MockMvcResultMatchers.content().string(containsString("19:00～22:00")));

    }

}
