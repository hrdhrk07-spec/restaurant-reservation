package com.example.restaurantreservation.service;

import com.example.restaurantreservation.entity.Reservation;
import com.example.restaurantreservation.entity.ReservationStatus;
import com.example.restaurantreservation.entity.SeatDetail;
import com.example.restaurantreservation.repository.ReservationRepository;
import com.example.restaurantreservation.repository.SeatDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 予約関連のビジネスロジックを記載したサービスクラス
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final SeatDetailRepository seatDetailRepository;

    /**
     * IDに一致する予約を1件取得
     *
     * @return 予約
     */
    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    /**
     * ユーザIDに一致する予約の全件取得
     *
     * @param userId ユーザID
     * @return 予約のリスト
     */
    public List<Reservation> getReservationsByUserId(Long userId) {
        return reservationRepository.findByUserIdOrderByReservedAtDesc(userId);
    }

    /**
     * 予約の全件取得
     *
     * @return 予約のリスト
     */
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    /**
     * 予約時の空席確認
     *
     * @param restaurantId   レストランID
     * @param reservedAt     予約日時
     * @param numberOfGuests 予約人数
     * @return 席詳細のリスト
     */
    public List<SeatDetail> getAvailableSeats(Long restaurantId, LocalDateTime reservedAt, int numberOfGuests) {
        // 予約人数から利用可能な席詳細を取得
        List<SeatDetail> seatDetailList = seatDetailRepository.findAvailableSeatsByRestaurantAndGuests(restaurantId, numberOfGuests);

        // 予約の重複確認用に予約のステータスを設定
        List<String> statuses = List.of(ReservationStatus.PENDING.name(), ReservationStatus.CONFIRMED.name());

        // 席の空き数が1以上ある席詳細を戻り値に設定
        return seatDetailList.stream()
                .filter(seatDetail -> seatDetail.getNumberOfSeats() -
                        reservationRepository.countOverlapping(
                                seatDetail.getId(),
                                reservedAt,
                                reservedAt.plusMinutes(seatDetail.getDuration()),
                                statuses
                        ) >= 1)
                .collect(Collectors.toList());

    }

    /**
     * 予約の登録
     *
     * @param reservation 予約
     */
    public void saveReservation(Reservation reservation) {
        reservationRepository.save(reservation);
    }

    /**
     * 予約のキャンセル
     *
     * @param reservation 予約
     */
    public void cancelReservation(Reservation reservation) {

        // ステータスをキャンセルにセット
        reservation.setStatus(ReservationStatus.CANCELLED);

        // 予約情報を更新
        reservationRepository.save(reservation);

    }

    /**
     * 予約のステータス更新
     *
     * @param id     予約ID
     * @param status ステータス
     */
    public void changeReservationStatus(Long id, ReservationStatus status) {
        Optional<Reservation> optionalReservation = reservationRepository.findById(id);
        Reservation reservation = optionalReservation.orElseThrow();
        reservation.setStatus(status);
        reservationRepository.save(reservation);
    }

}
