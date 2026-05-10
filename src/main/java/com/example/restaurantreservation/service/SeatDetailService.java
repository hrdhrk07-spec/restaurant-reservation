package com.example.restaurantreservation.service;

import com.example.restaurantreservation.entity.SeatDetail;
import com.example.restaurantreservation.exception.ResourceNotFoundException;
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
     * @param id 席詳細ID
     * @return 席詳細
     */
    public SeatDetail getSeatDetailById(Long id) {
        return seatDetailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("席詳細取得の失敗 ID:" + id));
    }

}
