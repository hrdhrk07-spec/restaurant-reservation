package com.example.restaurantreservation.service;

import com.example.restaurantreservation.entity.*;
import com.example.restaurantreservation.enums.HolidayDayOfWeek;
import com.example.restaurantreservation.form.ReservationForm;
import com.example.restaurantreservation.repository.HolidayRepository;
import com.example.restaurantreservation.repository.ReservationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    // テスト対象
    @Spy
    @InjectMocks
    private ReservationService reservationService;

    // モック
    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private HolidayRepository holidayRepository;

    // スパイ
    @Spy
    // 第１引数はUTC基準の時刻、第２引数は表示するタイムゾーンなので10時を指定
    private Clock clock = Clock.fixed(Instant.parse("2026-05-20T10:00:00Z"), ZoneId.of("Asia/Tokyo"));

    @ParameterizedTest
    @MethodSource("isHolidayProvider")
    @DisplayName("定休日チェック")
    void isHoliday(LocalDateTime reservedAt, List<Holiday> holidays, boolean expected) {
        when(holidayRepository.findByRestaurantId(any())).thenReturn(holidays);
        assertEquals(expected, reservationService.isHoliday(0L, reservedAt));
    }

    private static Holiday createHoliday(HolidayDayOfWeek dayOfWeek) {
        Holiday holiday = new Holiday();
        holiday.setHolidayDayOfWeek(dayOfWeek);
        return holiday;
    }

    static Stream<Arguments> isHolidayProvider() {

        List<Holiday> holidaysIncludingWed = List.of(
                createHoliday(HolidayDayOfWeek.SATURDAY),
                createHoliday(HolidayDayOfWeek.WEDNESDAY)
        );
        List<Holiday> holidaysExcludingWed = List.of(
                createHoliday(HolidayDayOfWeek.SATURDAY),
                createHoliday(HolidayDayOfWeek.SUNDAY)
        );
        List<Holiday> emptyHolidays = new ArrayList<>();


        return Stream.of(
                arguments(LocalDateTime.of(2026, 5, 20, 19, 0, 0), holidaysIncludingWed, true),
                arguments(LocalDateTime.of(2026, 5, 20, 19, 0, 0), holidaysExcludingWed, false),
                arguments(LocalDateTime.of(2026, 5, 20, 19, 0, 0), emptyHolidays, false)
        );
    }

    @ParameterizedTest
    @MethodSource("canReceptionProvider")
    @DisplayName("受付時間内チェック")
    void canReception(LocalDateTime reservedAt, LocalTime receptionStartTime, LocalTime receptionEndTime, boolean expected) {
        assertEquals(expected, ReservationService.canReception(reservedAt, receptionStartTime, receptionEndTime));
    }

    static Stream<Arguments> canReceptionProvider() {
        return Stream.of(
                // nullチェック
                arguments(LocalDateTime.of(2026, 1, 1, 23, 0), null, LocalTime.of(20, 0), true),
                arguments(LocalDateTime.of(2026, 1, 1, 16, 0), LocalTime.of(17, 0), null, true),
                arguments(LocalDateTime.of(2026, 1, 1, 18, 0), null, null, true),

                // 日をまたがない場合
                arguments(LocalDateTime.of(2026, 1, 1, 19, 0), LocalTime.of(17, 0), LocalTime.of(23, 0), true),
                arguments(LocalDateTime.of(2026, 1, 1, 15, 0), LocalTime.of(17, 0), LocalTime.of(23, 0), false),
                arguments(LocalDateTime.of(2026, 1, 1, 23, 30), LocalTime.of(17, 0), LocalTime.of(23, 0), false),

                // 境界値チェック
                arguments(LocalDateTime.of(2026, 1, 1, 16, 59), LocalTime.of(17, 0), LocalTime.of(23, 0), false),
                arguments(LocalDateTime.of(2026, 1, 1, 17, 0), LocalTime.of(17, 0), LocalTime.of(23, 0), true),
                arguments(LocalDateTime.of(2026, 1, 1, 17, 1), LocalTime.of(17, 0), LocalTime.of(23, 0), true),
                arguments(LocalDateTime.of(2026, 1, 1, 22, 59), LocalTime.of(17, 0), LocalTime.of(23, 0), true),
                arguments(LocalDateTime.of(2026, 1, 1, 23, 0), LocalTime.of(17, 0), LocalTime.of(23, 0), true),
                arguments(LocalDateTime.of(2026, 1, 1, 23, 1), LocalTime.of(17, 0), LocalTime.of(23, 0), false),

                // 日をまたぐ場合
                arguments(LocalDateTime.of(2026, 1, 1, 22, 0), LocalTime.of(18, 0), LocalTime.of(3, 0), true),
                arguments(LocalDateTime.of(2026, 1, 1, 1, 30), LocalTime.of(18, 0), LocalTime.of(3, 0), true),
                arguments(LocalDateTime.of(2026, 1, 1, 15, 0), LocalTime.of(18, 0), LocalTime.of(3, 0), false),
                arguments(LocalDateTime.of(2026, 1, 1, 5, 0), LocalTime.of(18, 0), LocalTime.of(3, 0), false),

                // 境界値チェック
                arguments(LocalDateTime.of(2026, 1, 1, 17, 59), LocalTime.of(18, 0), LocalTime.of(3, 0), false),
                arguments(LocalDateTime.of(2026, 1, 1, 18, 0), LocalTime.of(18, 0), LocalTime.of(3, 0), true),
                arguments(LocalDateTime.of(2026, 1, 1, 18, 1), LocalTime.of(18, 0), LocalTime.of(3, 0), true),
                arguments(LocalDateTime.of(2026, 1, 1, 23, 59), LocalTime.of(18, 0), LocalTime.of(3, 0), true),
                arguments(LocalDateTime.of(2026, 1, 1, 0, 0), LocalTime.of(18, 0), LocalTime.of(3, 0), true),
                arguments(LocalDateTime.of(2026, 1, 1, 0, 1), LocalTime.of(18, 0), LocalTime.of(3, 0), true),
                arguments(LocalDateTime.of(2026, 1, 1, 2, 59), LocalTime.of(18, 0), LocalTime.of(3, 0), true),
                arguments(LocalDateTime.of(2026, 1, 1, 3, 0), LocalTime.of(18, 0), LocalTime.of(3, 0), true),
                arguments(LocalDateTime.of(2026, 1, 1, 3, 1), LocalTime.of(18, 0), LocalTime.of(3, 0), false)
        );
    }

    @ParameterizedTest
    @MethodSource("isPastDateProvider")
    @DisplayName("過去日時チェック")
    void isPastDate(LocalDateTime reservedAt, boolean expected) {
        assertEquals(expected, reservationService.isPastDate(reservedAt));
    }

    static Stream<Arguments> isPastDateProvider() {
        return Stream.of(
                arguments(LocalDateTime.of(2026, 5, 20, 15, 0, 0), true),
                arguments(LocalDateTime.of(2026, 5, 20, 19, 0, 0), false),
                arguments(LocalDateTime.of(2026, 5, 20, 22, 0, 0), false),
                arguments(LocalDateTime.of(2026, 5, 20, 18, 59, 59), true),
                arguments(LocalDateTime.of(2026, 5, 20, 19, 0, 1), false)
        );
    }

    @Test
    @DisplayName("過去日時への予約")
    void saveReservation_isPastDate() {

        // 後述のチェックは予約日時にかかわらず結果を返すため、ここでは予約日時を設定しない
        ReservationForm reservationForm = new ReservationForm();

        // isPastDateがtrueを返すようにモック
        doReturn(true).when(reservationService).isPastDate(any());

        // 期待結果
        assertThrows(
                RuntimeException.class,
                () -> reservationService.saveReservation(mock(User.class), mock(SeatDetail.class), reservationForm)
        );
    }

    @Test
    @DisplayName("受付時間外への予約")
    void saveReservation_canReception() {

        // 席詳細がレストランを返せるようにモック
        Restaurant restaurant = mock(Restaurant.class);
        SeatDetail seatDetail = mock(SeatDetail.class);
        when(seatDetail.getRestaurant()).thenReturn(restaurant);

        // このケースではcanReceptionがfalseを返すように予約日時を設定
        ReservationForm reservationForm = new ReservationForm();
        reservationForm.setReservedAt(LocalDateTime.of(2026,5,20,15,0,0));

        // isPastDateがfalseを返すようにモック
        doReturn(false).when(reservationService).isPastDate(any());

        // canReceptionがfalseを返すように受付時間をモック
        when(restaurant.getReceptionStartTime()).thenReturn(LocalTime.of(19,0));
        when(restaurant.getReceptionEndTime()).thenReturn(LocalTime.of(22,0));

        // 期待結果
        assertThrows(
                RuntimeException.class,
                () -> reservationService.saveReservation(mock(User.class), seatDetail, reservationForm)
        );

    }

    @Test
    @DisplayName("定休日への予約")
    void saveReservation_isHoliday() {

        // 後述のチェックは予約日時にかかわらず結果を返すため、ここでは予約日時を設定しない
        ReservationForm reservationForm = new ReservationForm();

        // 席詳細がレストランを返せるようにモック
        Restaurant restaurant = mock(Restaurant.class);
        SeatDetail seatDetail = mock(SeatDetail.class);
        when(seatDetail.getRestaurant()).thenReturn(restaurant);

        // isPastDateがfalseを返すようにモック
        doReturn(false).when(reservationService).isPastDate(any());

        // isHolidayがtrueを返すようにモック
        doReturn(true).when(reservationService).isHoliday(any(), any());

        // 期待結果
        assertThrows(
                RuntimeException.class,
                () -> reservationService.saveReservation(mock(User.class), seatDetail, reservationForm)
        );

    }

    @Test
    @DisplayName("席重複チェックNG")
    void saveReservation_noSeats() {

        // 席詳細がレストランを返せるようにモック
        Restaurant restaurant = mock(Restaurant.class);
        SeatDetail seatDetail = mock(SeatDetail.class);
        when(seatDetail.getRestaurant()).thenReturn(restaurant);

        // このケースではcanReceptionがtrueを返すように予約日時を設定
        ReservationForm reservationForm = new ReservationForm();
        reservationForm.setReservedAt(LocalDateTime.of(2026,5,20,20,0,0));

        // isPastDateがfalseを返すようにモック
        doReturn(false).when(reservationService).isPastDate(any());

        // isHolidayがfalseを返すようにモック
        doReturn(false).when(reservationService).isHoliday(any(), any());

        // canReceptionがtrueを返すように受付時間をモック
        when(restaurant.getReceptionStartTime()).thenReturn(LocalTime.of(19,0));
        when(restaurant.getReceptionEndTime()).thenReturn(LocalTime.of(22,0));

        // 席詳細の席セット数で2を取得するようモック
        when(seatDetail.getNumberOfSeats()).thenReturn(2);

        // 重複している予約の数で2を取得するようモック
        when(reservationRepository.countOverlapping(any(),any(),any(),any())).thenReturn(2);

        // 期待結果
        assertThrows(
                RuntimeException.class,
                () -> reservationService.saveReservation(mock(User.class), seatDetail, reservationForm)
        );

    }

    @Test
    @DisplayName("予約正常系")
    void saveReservation_OK() {

        // 席詳細がレストランを返せるようにモック
        Restaurant restaurant = mock(Restaurant.class);
        SeatDetail seatDetail = mock(SeatDetail.class);
        when(seatDetail.getRestaurant()).thenReturn(restaurant);

        // このケースではcanReceptionがtrueを返すように予約日時を設定
        ReservationForm reservationForm = new ReservationForm();
        reservationForm.setReservedAt(LocalDateTime.of(2026,5,20,20,0,0));

        // isPastDateがfalseを返すようにモック
        doReturn(false).when(reservationService).isPastDate(any());

        // isHolidayがfalseを返すようにモック
        doReturn(false).when(reservationService).isHoliday(any(), any());

        // canReceptionがtrueを返すように受付時間をモック
        when(restaurant.getReceptionStartTime()).thenReturn(LocalTime.of(19,0));
        when(restaurant.getReceptionEndTime()).thenReturn(LocalTime.of(22,0));

        // 席詳細の席セット数で2を取得するようモック
        when(seatDetail.getNumberOfSeats()).thenReturn(2);

        // 重複している予約の数で1を取得するようモック
        when(reservationRepository.countOverlapping(any(),any(),any(),any())).thenReturn(1);

        // 予約人数のセット
        reservationForm.setNumberOfGuests(2);

        // 予約をモック
        Reservation reservation = mock(Reservation.class);

        // 登録処理のモック
        when(reservationRepository.save(any())).thenReturn(reservation);

        // 期待結果
        assertEquals(reservationService.saveReservation(mock(User.class), seatDetail, reservationForm), reservation);
        verify(reservationRepository, times(1)).save(any());

    }

}