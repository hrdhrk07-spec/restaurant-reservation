package com.example.restaurantreservation.repository;

import com.example.restaurantreservation.config.JpaAuditingConfig;
import com.example.restaurantreservation.entity.Reservation;
import com.example.restaurantreservation.entity.SeatDetail;
import com.example.restaurantreservation.enums.ReservationStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Import(JpaAuditingConfig.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql("/test-data.sql")
class ReservationRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SeatDetailRepository seatDetailRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private ReservationRepository reservationRepository;


    private SeatDetail insertExistingReservation(ReservationStatus status){

        // 既存の予約情報の作成
        SeatDetail seatDetail = seatDetailRepository.findById(9999999L).get();
        Reservation reservation = new Reservation();
        reservation.setUser(userRepository.findById(9999999L).get());
        reservation.setSeatDetail(seatDetail);
        reservation.setRestaurant(restaurantRepository.findById(9999999L).get());
        reservation.setReservedAt(LocalDateTime.of(2026, 5, 27, 18, 0));
        reservation.setNumberOfGuests(2);
        reservation.setStatus(status);
        reservationRepository.save(reservation);

        // 席詳細を返す
        return seatDetail;
    }

    @Test
    @DisplayName("重複予約が無い場合、0を返す")
    void countOverlapping_noReservation() {

        SeatDetail seatDetail = seatDetailRepository.findById(9999999L).get();

        assertEquals(0, reservationRepository.countOverlapping(
                seatDetail.getId(),
                LocalDateTime.of(2026, 5, 27, 17, 10),
                LocalDateTime.of(2026, 5, 27, 17, 50),
                ReservationStatus.CONFIRMED.name()
        ));

    }

    @Test
    @DisplayName("重複予約が1件の場合、1を返す")
    void countOverlapping_oneSeatConfirmed() {

        // 既存の予約情報の作成
        SeatDetail seatDetail = insertExistingReservation(ReservationStatus.CONFIRMED);

        assertEquals(1, reservationRepository.countOverlapping(
                seatDetail.getId(),
                LocalDateTime.of(2026, 5, 27, 18, 10),
                LocalDateTime.of(2026, 5, 27, 18, 50),
                ReservationStatus.CONFIRMED.name()
        ));

    }

    @Test
    @DisplayName("重複予約が2件でCONFIRMが1件、CANCELLEDが1件の場合、1を返す")
    void countOverlapping_ConfirmedAndCancelled() {

        // 既存の予約情報の作成
        SeatDetail seatDetail = insertExistingReservation(ReservationStatus.CONFIRMED);
        insertExistingReservation(ReservationStatus.CANCELLED);

        assertEquals(1, reservationRepository.countOverlapping(
                seatDetail.getId(),
                LocalDateTime.of(2026, 5, 27, 18, 10),
                LocalDateTime.of(2026, 5, 27, 18, 50),
                ReservationStatus.CONFIRMED.name()
        ));

    }

    @Test
    @DisplayName("既存予約より前の時間帯の場合、0を返す")
    void countOverlapping_beforeExistingReservation() {

        // 既存の予約情報の作成
        SeatDetail seatDetail = insertExistingReservation(ReservationStatus.CONFIRMED);

        assertEquals(0, reservationRepository.countOverlapping(
                seatDetail.getId(),
                LocalDateTime.of(2026, 5, 27, 17, 0),
                LocalDateTime.of(2026, 5, 27, 18, 0),
                ReservationStatus.CONFIRMED.name()
        ));

    }

    @Test
    @DisplayName("既存予約の前に開始、途中で終了の時間帯の場合、1を返す")
    void countOverlapping_overlapsStartOfExistingReservation() {

        // 既存の予約情報の作成
        SeatDetail seatDetail = insertExistingReservation(ReservationStatus.CONFIRMED);

        assertEquals(1, reservationRepository.countOverlapping(
                seatDetail.getId(),
                LocalDateTime.of(2026, 5, 27, 17, 30),
                LocalDateTime.of(2026, 5, 27, 18, 30),
                ReservationStatus.CONFIRMED.name()
        ));

    }

    @Test
    @DisplayName("既存予約と完全一致の時間帯の場合、1を返す")
    void countOverlapping_overlapsExistingReservation() {

        // 既存の予約情報の作成
        SeatDetail seatDetail = insertExistingReservation(ReservationStatus.CONFIRMED);

        assertEquals(1, reservationRepository.countOverlapping(
                seatDetail.getId(),
                LocalDateTime.of(2026, 5, 27, 18, 0),
                LocalDateTime.of(2026, 5, 27, 19, 0),
                ReservationStatus.CONFIRMED.name()
        ));

    }

    @Test
    @DisplayName("既存予約の後に開始、終了後に終了の時間帯の場合、1を返す")
    void countOverlapping_overlapsEndOfExistingReservation() {

        // 既存の予約情報の作成
        SeatDetail seatDetail = insertExistingReservation(ReservationStatus.CONFIRMED);

        assertEquals(1, reservationRepository.countOverlapping(
                seatDetail.getId(),
                LocalDateTime.of(2026, 5, 27, 18, 30),
                LocalDateTime.of(2026, 5, 27, 19, 30),
                ReservationStatus.CONFIRMED.name()
        ));

    }

    @Test
    @DisplayName("既存予約の後の時間帯の場合、0を返す")
    void countOverlapping_afterExistingReservation() {

        // 既存の予約情報の作成
        SeatDetail seatDetail = insertExistingReservation(ReservationStatus.CONFIRMED);

        assertEquals(0, reservationRepository.countOverlapping(
                seatDetail.getId(),
                LocalDateTime.of(2026, 5, 27, 19, 0),
                LocalDateTime.of(2026, 5, 27, 20, 0),
                ReservationStatus.CONFIRMED.name()
        ));

    }

}
