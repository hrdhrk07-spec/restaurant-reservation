package com.example.restaurantreservation.controller;

import com.example.restaurantreservation.entity.*;
import com.example.restaurantreservation.enums.HolidayDayOfWeek;
import com.example.restaurantreservation.exception.ResourceNotFoundException;
import com.example.restaurantreservation.exception.UnauthorizedAccessException;
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

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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

    private Reservation setReservation(User user, Restaurant restaurant, SeatDetail seatDetail) {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setUser(user);
        reservation.setRestaurant(restaurant);
        reservation.setSeatDetail(seatDetail);
        reservation.setReservedAt(LocalDateTime.now());
        reservation.setNumberOfGuests(2);
        return reservation;
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

        restaurant.setHolidays(new ArrayList<>(Arrays.asList(holiday1, holiday2)));

        // 席詳細の設定
        seatDetails = new ArrayList<>();
        seatDetails.add(setSeatDetail(1001L, 1, 2, 60));
        seatDetails.add(setSeatDetail(1002L, 2, 4, 120));

    }

    @Test
    @DisplayName("予約入力画面(GET)")
    void input() throws Exception {

        // モックの設定
        when(restaurantService.getRestaurantById(any())).thenReturn(restaurant);

        // リクエストの実行と期待結果の検証
        mockMvc.perform(MockMvcRequestBuilders.get("/reservation-input/1000"))
                .andExpectAll(
                        view().name("user/reservation-input"),
                        model().attribute("restaurant", restaurant),
                        model().attribute("reservationForm", new ReservationForm())
                );

    }

    @Test
    @DisplayName("予約入力画面(POST)")
    void inputPost() throws Exception {

        // モックの設定
        when(restaurantService.getRestaurantById(any())).thenReturn(restaurant);
        when(reservationService.isHoliday(any(), any())).thenReturn(false);
        when(reservationService.getAvailableSeats(any(), any(), anyInt())).thenReturn(seatDetails);

        // 予約日時の設定
        LocalDate date = LocalDate.now(ZoneId.of("Asia/Tokyo")).plusDays(1L);
        while (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            date = date.plusDays(1L);
        }
        LocalDateTime time = LocalDateTime.of(date, LocalTime.of(20, 0));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        String reservedAt = time.format(formatter);

        // リクエストの実行と期待結果の検証
        // reservationFormはリクエストパラメータがバインドされたオブジェクトなので存在確認のみ
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/reservation-input/1000")
                        .sessionAttr("reservationForm", new ReservationForm())
                        .with(csrf())
                        .param("reservedAt", reservedAt)
                        .param("numberOfGuests", "2")
                )
                .andExpectAll(
                        view().name("user/reservation-input"),
                        model().attribute("restaurant", restaurant),
                        model().attribute("seatDetailList", seatDetails),
                        model().attributeExists("reservationForm")
                );

    }

    @Test
    @DisplayName("予約入力画面(POST)_バリデーションエラー")
    void inputPost_validationError() throws Exception {

        // モックの設定
        when(restaurantService.getRestaurantById(any())).thenReturn(restaurant);

        // リクエストの実行と期待結果の検証
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/reservation-input/1000")
                        .sessionAttr("reservationForm", new ReservationForm())
                        .with(csrf())
                        .param("reservedAt", "")
                        .param("numberOfGuests", "")
                )
                .andExpectAll(
                        view().name("user/reservation-input"),
                        model().attribute("restaurant", restaurant)
                );

    }

    @Test
    @DisplayName("予約入力画面(POST)_過去日時エラー")
    void inputPost_pastDateError() throws Exception {

        // モックの設定
        when(restaurantService.getRestaurantById(any())).thenReturn(restaurant);
        when(reservationService.isPastDate(any())).thenReturn(true);

        // リクエストの実行と期待結果の検証
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/reservation-input/1000")
                        .sessionAttr("reservationForm", new ReservationForm())
                        .with(csrf())
                        .param("reservedAt", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")))
                        .param("numberOfGuests", "2")
                )
                .andExpectAll(
                        view().name("user/reservation-input"),
                        model().attribute("restaurant", restaurant),
                        model().attribute("pastDateError", not(emptyOrNullString()))
                );

    }

    @Test
    @DisplayName("予約入力画面(POST)_受付時間外エラー")
    void inputPost_receptionError() throws Exception {

        // モックの設定
        when(restaurantService.getRestaurantById(any())).thenReturn(restaurant);

        // 受付時間外となる予約日時の設定
        LocalDate date = LocalDate.now(ZoneId.of("Asia/Tokyo")).plusDays(1L);
        while (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            date = date.plusDays(1L);
        }
        LocalDateTime time = LocalDateTime.of(date, LocalTime.of(15, 0));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        String reservedAt = time.format(formatter);

        // リクエストの実行と期待結果の検証
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/reservation-input/1000")
                        .sessionAttr("reservationForm", new ReservationForm())
                        .with(csrf())
                        .param("reservedAt", reservedAt)
                        .param("numberOfGuests", "2")
                )
                .andExpectAll(
                        view().name("user/reservation-input"),
                        model().attribute("restaurant", restaurant),
                        model().attribute("receptionError", not(emptyOrNullString()))
                );

    }

    @Test
    @DisplayName("予約入力画面(POST)_定休日エラー")
    void inputPost_holidayError() throws Exception {

        // モックの設定
        when(restaurantService.getRestaurantById(any())).thenReturn(restaurant);
        when(reservationService.isHoliday(any(), any())).thenReturn(true);

        // 受付時間外エラーにならない予約日時の設定
        LocalDate date = LocalDate.now(ZoneId.of("Asia/Tokyo")).plusDays(1L);
        LocalDateTime time = LocalDateTime.of(date, LocalTime.of(20, 0));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        String reservedAt = time.format(formatter);

        // リクエストの実行と期待結果の検証
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/reservation-input/1000")
                        .sessionAttr("reservationForm", new ReservationForm())
                        .with(csrf())
                        .param("reservedAt", reservedAt)
                        .param("numberOfGuests", "2")
                )
                .andExpectAll(
                        view().name("user/reservation-input"),
                        model().attribute("restaurant", restaurant),
                        model().attribute("holidayError", not(emptyOrNullString()))
                );

    }

    @Test
    @DisplayName("予約確認画面(POST)")
    void confirmPost() throws Exception {

        // モックの設定
        when(seatDetailService.getSeatDetailById(any())).thenReturn(seatDetails.getFirst());

        // 予約フォームの設定
        // ここでは予約日時のチェックは無いため任意
        ReservationForm reservationForm = new ReservationForm();
        reservationForm.setReservedAt(LocalDateTime.now());
        reservationForm.setNumberOfGuests(2);


        // リクエストの実行と期待結果の検証
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/reservation-confirm")
                        .sessionAttr("reservationForm", reservationForm)
                        .with(csrf())
                        .param("seatDetailId", "1001")
                )
                .andExpectAll(
                        view().name("user/reservation-confirm"),
                        model().attribute("restaurant", restaurant),
                        model().attribute("seatDetail", seatDetails.getFirst()),
                        model().attribute("reservationForm", reservationForm)
                );

    }

    @Test
    @DisplayName("予約確認画面(POST)_席詳細取得失敗エラー")
    void confirmPost_noSeatDetailId() throws Exception {

        // リクエストの実行と期待結果の検証
        // seatDetailIdをparamに含めないことでエラーを起こす
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/reservation-confirm")
                        .sessionAttr("reservationForm", new ReservationForm())
                        .with(csrf())
                )
                .andExpect(result ->
                        assertInstanceOf(ResourceNotFoundException.class, result.getResolvedException())
                );

    }

    @Test
    @DisplayName("予約完了画面(POST)")
    void completePost() throws Exception {

        // 予約の作成
        Reservation reservation = new Reservation();
        reservation.setRestaurant(restaurant);

        // モックの設定
        when(userService.getUserByEmail(any())).thenReturn(new User());
        when(seatDetailService.getSeatDetailById(any())).thenReturn(seatDetails.getFirst());
        when(reservationService.saveReservation(any(), any(), any())).thenReturn(reservation);

        // リクエストの実行と期待結果の検証
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/reservation-complete")
                        .sessionAttr("reservationForm", new ReservationForm())
                        .with(csrf())
                        .param("seatDetailId", "1001")
                )
                .andExpectAll(
                        view().name("user/reservation-complete"),
                        model().attribute("reservation", reservation)
                );

        // DB登録処理を呼び出したかを検証
        verify(reservationService).saveReservation(any(), any(), any());

    }

    @Test
    @DisplayName("予約一覧画面(GET)")
    void list() throws Exception {

        // 予約リストの作成
        List<Reservation> reservations = new ArrayList<>();

        // モックの設定
        when(userService.getUserByEmail(any())).thenReturn(new User());
        when(reservationService.getReservationsByUserId(any())).thenReturn(reservations);

        // リクエストの実行と期待結果の検証
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/reservation-list")
                )
                .andExpectAll(
                        view().name("user/reservation-list"),
                        model().attribute("reservationList", reservations)
                );

    }

    @Test
    @DisplayName("キャンセル確認画面(GET)")
    void cancelConfirm() throws Exception {

        // ユーザの作成
        User user = new User();
        user.setId(1L);

        // 予約の作成
        Reservation reservation = setReservation(user,restaurant,seatDetails.getFirst());

        // モックの設定
        when(reservationService.getReservationById(any())).thenReturn(reservation);
        when(userService.getUserByEmail(any())).thenReturn(user);

        // リクエストの実行と期待結果の検証
        mockMvc.perform(MockMvcRequestBuilders.get("/cancel-confirm/1"))
                .andExpectAll(
                        view().name("user/cancel-confirm"),
                        model().attribute("reservation", reservation)
                );

    }

    @Test
    @DisplayName("キャンセル確認画面(GET)_予約情報が別のユーザ")
    void cancelConfirm_anotherUser() throws Exception {

        // ユーザの作成
        User user1 = new User();
        user1.setId(1L);

        User user2 = new User();
        user2.setId(2L);

        // 予約の作成
        Reservation reservation = setReservation(user1,restaurant,seatDetails.getFirst());

        // モックの設定
        when(reservationService.getReservationById(any())).thenReturn(reservation);
        when(userService.getUserByEmail(any())).thenReturn(user2);

        // リクエストの実行と期待結果の検証
        mockMvc.perform(MockMvcRequestBuilders.get("/cancel-confirm/1"))
                .andExpect(result ->
                assertInstanceOf(UnauthorizedAccessException.class, result.getResolvedException())
        );

    }

    @Test
    @DisplayName("キャンセル完了画面(POST)")
    void cancelPost() throws Exception {

        // ユーザの作成
        User user = new User();
        user.setId(1L);

        // 予約の作成
        Reservation reservation = setReservation(user,restaurant,seatDetails.getFirst());

        // モックの設定
        when(reservationService.getReservationById(any())).thenReturn(reservation);
        when(userService.getUserByEmail(any())).thenReturn(user);
        doNothing().when(reservationService).cancelReservation(any());

        // リクエストの実行と期待結果の検証
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/cancel-complete")
                        .param("id", "1")
                        .with(csrf()))
                .andExpect(view().name("user/cancel-complete"));

        // DB更新処理を呼び出したかを検証
        verify(reservationService).cancelReservation(any());

    }

    @Test
    @DisplayName("キャンセル完了画面(POST)_予約情報が別のユーザ")
    void cancelPost_anotherUser() throws Exception {

        // ユーザの作成
        User user1 = new User();
        user1.setId(1L);

        User user2 = new User();
        user2.setId(2L);

        // 予約の作成
        Reservation reservation = setReservation(user1,restaurant,seatDetails.getFirst());

        // モックの設定
        when(reservationService.getReservationById(any())).thenReturn(reservation);
        when(userService.getUserByEmail(any())).thenReturn(user2);
        doNothing().when(reservationService).cancelReservation(any());

        // リクエストの実行と期待結果の検証
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/cancel-complete")
                        .param("id", "1")
                        .with(csrf()))
                .andExpect(result ->
                        assertInstanceOf(UnauthorizedAccessException.class, result.getResolvedException())
                );

    }

}
