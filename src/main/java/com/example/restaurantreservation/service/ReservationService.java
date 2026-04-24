package com.example.restaurantreservation.service;

import com.example.restaurantreservation.entity.Reservation;
import com.example.restaurantreservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 予約関連のビジネスロジックを記載したサービスクラス
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    /**
     * 予約の全件取得
     *
     * @return 予約のリスト
     */
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    /**
     * IDに一致する予約の全件取得
     *
     * @param userId ユーザID
     * @return 予約のリスト
     */
    public List<Reservation> getReservationsByUserId(Long userId) {
        return reservationRepository.findByUserId(userId);
    }

    /**
     * 予約の登録
     *
     * @param reservation 予約
     * @return 登録した予約
     */
    public Reservation saveReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

}
