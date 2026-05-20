package com.example.restaurantreservation.service;

import com.example.restaurantreservation.entity.Holiday;
import com.example.restaurantreservation.entity.Restaurant;
import com.example.restaurantreservation.enums.HolidayDayOfWeek;
import com.example.restaurantreservation.repository.HolidayRepository;
import com.example.restaurantreservation.repository.ReservationRepository;
import com.example.restaurantreservation.repository.SeatDetailRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReservationServiceTest {

    private ReservationService reservationService;
    private ReservationRepository reservationRepository;
    private SeatDetailRepository seatDetailRepository;
    private HolidayRepository holidayRepository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        this.reservationRepository = mock(ReservationRepository.class);
        this.seatDetailRepository = mock(SeatDetailRepository.class);
        this.holidayRepository = mock(HolidayRepository.class);
        // 第１引数はUTC基準の時刻、第２引数は表示するタイムゾーンなので10時を指定
        this.clock = Clock.fixed(Instant.parse("2026-05-20T10:00:00Z"), ZoneId.of("Asia/Tokyo"));
        this.reservationService = new ReservationService(reservationRepository, seatDetailRepository, holidayRepository, clock);
    }

    @ParameterizedTest
    @MethodSource("isHolidayProvider")
    @DisplayName("定休日チェック")
    void isHoliday(LocalDateTime reservedAt, List<Holiday> holidays, boolean expected) {
        when(holidayRepository.findByRestaurantId(anyLong())).thenReturn(holidays);
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

}