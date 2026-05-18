package com.example.restaurantreservation.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class ReservationServiceTest {

    @ParameterizedTest
    @MethodSource("canReceptionProvider")
    @DisplayName("受付時間内チェックテスト")
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

}