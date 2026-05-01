package com.example.restaurantreservation.service;

import com.example.restaurantreservation.entity.SeatDetail;
import com.example.restaurantreservation.repository.SeatDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 席詳細関連のビジネスロジックを記載したサービスクラス
 */
@Service
@Transactional
@RequiredArgsConstructor
public class SeatDetailService {

    private final SeatDetailRepository seatDetailRepository;

    /**
     * IDに一致する席詳細を1件取得
     *
     * @param id    席詳細ID
     * @return 席詳細
     */
    public Optional<SeatDetail> getSeatDetailById(Long id) {
        return seatDetailRepository.findById(id);
    }

    /**
     * レストランIDに一致する席詳細の全件取得
     *
     * @param restaurantId レストランID
     * @return 席詳細のリスト
     */
    public List<SeatDetail> getAllSeatDetailsByRestaurantId(Long restaurantId) {
        return seatDetailRepository.findByRestaurantId(restaurantId);
    }

    /**
     * 席詳細の登録
     *
     * @param seatDetail 席詳細
     * @return 登録した席詳細
     */
    public SeatDetail saveSeatDetail(SeatDetail seatDetail) {
        return seatDetailRepository.save(seatDetail);
    }

    /**
     * IDに一致した席詳細の削除
     *
     * @param id 席詳細ID
     */
    public void deleteSeatDetail(Long id) {
        seatDetailRepository.deleteById(id);
    }

}
