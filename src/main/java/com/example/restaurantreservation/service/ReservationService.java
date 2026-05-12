package com.example.restaurantreservation.service;

import com.example.restaurantreservation.entity.Reservation;
import com.example.restaurantreservation.entity.ReservationStatus;
import com.example.restaurantreservation.entity.SeatDetail;
import com.example.restaurantreservation.entity.User;
import com.example.restaurantreservation.exception.ResourceNotFoundException;
import com.example.restaurantreservation.form.ReservationForm;
import com.example.restaurantreservation.repository.ReservationRepository;
import com.example.restaurantreservation.repository.SeatDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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
    public Reservation getReservationById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("予約取得の失敗 ID:" + id));
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
        String status = ReservationStatus.CONFIRMED.name();

        // 席の空き数が1以上ある席詳細を戻り値に設定
        return seatDetailList.stream()
                .filter(seatDetail -> seatDetail.getNumberOfSeats() -
                        reservationRepository.countOverlapping(
                                seatDetail.getId(),
                                reservedAt,
                                reservedAt.plusMinutes(seatDetail.getDuration()),
                                status
                        ) >= 1)
                .collect(Collectors.toList());

    }

    /**
     * 予約の登録
     *
     * @param user            ユーザ
     * @param seatDetail      席詳細
     * @param reservationForm 予約登録フォーム
     */
    public Reservation saveReservation(User user, SeatDetail seatDetail, ReservationForm reservationForm) {

        // 予約情報をセット
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setSeatDetail(seatDetail);
        reservation.setRestaurant(seatDetail.getRestaurant());
        reservation.setReservedAt(reservationForm.getReservedAt());
        reservation.setNumberOfGuests(reservationForm.getNumberOfGuests());
        reservation.setStatus(ReservationStatus.CONFIRMED);

        // 重複している予約の数を取得
        int overlapping = reservationRepository.countOverlapping(
                seatDetail.getId(),
                reservationForm.getReservedAt(),
                reservationForm.getReservedAt().plusMinutes(seatDetail.getDuration()),
                ReservationStatus.CONFIRMED.name()
        );

        // 席が空いていない場合は例外処理
        if (seatDetail.getNumberOfSeats() - overlapping < 1) {
            throw new RuntimeException("予約時重複チェックの失敗 レストランID:" + seatDetail.getRestaurant().getId()
                    + " ユーザID:" + user.getId());
        }

        // 登録
        return reservationRepository.save(reservation);

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

        // 予約情報の取得
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("予約取得の失敗 ID:" + id));

        // ステータスをセットして登録
        reservation.setStatus(status);
        reservationRepository.save(reservation);
    }

}
