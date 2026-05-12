package com.example.restaurantreservation.service;

import com.example.restaurantreservation.entity.Holiday;
import com.example.restaurantreservation.repository.HolidayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 定休日関連のビジネスロジックを記載したサービスクラス
 */
@Service
@Transactional
@RequiredArgsConstructor
public class HolidayService {

    private final HolidayRepository holidayRepository;

    /**
     * レストランIDに一致する定休日の取得
     */
    public List<Holiday> getHolidaysByRestaurantId(Long id){
        return holidayRepository.findByRestaurantId(id);
    }

}
