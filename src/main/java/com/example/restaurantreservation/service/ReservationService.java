package com.example.restaurantreservation.service;

import com.example.restaurantreservation.entity.*;
import com.example.restaurantreservation.enums.HolidayDayOfWeek;
import com.example.restaurantreservation.enums.ReservationStatus;
import com.example.restaurantreservation.exception.ResourceNotFoundException;
import com.example.restaurantreservation.form.ReservationForm;
import com.example.restaurantreservation.repository.HolidayRepository;
import com.example.restaurantreservation.repository.ReservationRepository;
import com.example.restaurantreservation.repository.SeatDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    private final HolidayRepository holidayRepository;

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
     * 定休日チェック
     *
     * @param restaurantId レストランID
     * @param reservedAt   予約日時
     * @return 定休日ならTrue、そうでなければFalse
     */
    public boolean isHoliday(Long restaurantId, LocalDateTime reservedAt) {
        // 定休日の取得
        List<HolidayDayOfWeek> holidays = holidayRepository.findByRestaurantId(restaurantId).stream()
                .map(Holiday::getHolidayDayOfWeek)
                .toList();

        // 予約日の曜日が定休日に含まれていればTrueを返す
        return holidays.contains(HolidayDayOfWeek.of(reservedAt.getDayOfWeek()));

    }

    /**
     * 受付時間内チェック
     *
     * @param reservedAt         予約日時
     * @param receptionStartTime 受付開始時刻
     * @param receptionEndTime   受付終了時刻
     * @return 予約時刻が受付時間内であればTrue、そうでなければFalse
     */
    public boolean canReception(LocalDateTime reservedAt, LocalTime receptionStartTime, LocalTime receptionEndTime) {

        // 受付開始時刻と受付終了時刻のどちらか一方でもnullであれば予約可能とする
        if (receptionStartTime == null || receptionEndTime == null) {
            return true;
        }

        // LocalDateTime -> LocalTime変換
        LocalTime reservedAtTime = reservedAt.toLocalTime();

        if (receptionStartTime.isBefore(receptionEndTime)) {
            // 受付時間が日をまたがない場合は、「予約日時が受付開始時刻と受付終了時刻の間」であればOK
            // isBeforeとisAfterは境界値を含まないため、否定を利用して境界値を含む
            return !reservedAtTime.isBefore(receptionStartTime) && !reservedAtTime.isAfter(receptionEndTime);
        } else {
            // 受付時間が日をまたぐ場合は、「予約日時が受付開始時刻より後」または「予約日時が受付終了時刻より前」であればOK
            return !reservedAtTime.isBefore(receptionStartTime) || !reservedAtTime.isAfter(receptionEndTime);
        }

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
     * 過去日時チェック
     *
     * @param reservedAt 予約日時
     * @return 過去日時ならTrue、そうでなければFalse
     */
    public boolean isPastDate(LocalDateTime reservedAt) {
        return reservedAt.isBefore(LocalDateTime.now());
    }

    /**
     * 予約の登録
     *
     * @param user            ユーザ
     * @param seatDetail      席詳細
     * @param reservationForm 予約登録フォーム
     */
    public Reservation saveReservation(User user, SeatDetail seatDetail, ReservationForm reservationForm) {

        // 繰り返し使う情報をセット
        LocalDateTime reservedAt = reservationForm.getReservedAt();
        Restaurant restaurant = seatDetail.getRestaurant();

        // 過去日時チェック
        if (isPastDate(reservedAt)) {
            throw new RuntimeException("過去日時への予約");
        }

        // 定休日チェック
        if (isHoliday(restaurant.getId(), reservedAt)) {
            throw new RuntimeException("定休日への予約");
        }

        // 受付時間チェック
        if (!canReception(reservedAt, restaurant.getReceptionStartTime(), restaurant.getReceptionEndTime())) {
            throw new RuntimeException("受付時間外への予約");
        }

        // 重複している予約の数を取得
        int overlapping = reservationRepository.countOverlapping(
                seatDetail.getId(),
                reservedAt,
                reservedAt.plusMinutes(seatDetail.getDuration()),
                ReservationStatus.CONFIRMED.name()
        );

        // 席が空いていない場合は例外処理
        if (seatDetail.getNumberOfSeats() - overlapping < 1) {
            throw new RuntimeException("予約時重複チェックの失敗 レストランID:" + restaurant.getId()
                    + " ユーザID:" + user.getId());
        }

        // 予約情報をセット
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setSeatDetail(seatDetail);
        reservation.setRestaurant(restaurant);
        reservation.setReservedAt(reservedAt);
        reservation.setNumberOfGuests(reservationForm.getNumberOfGuests());
        reservation.setStatus(ReservationStatus.CONFIRMED);

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
