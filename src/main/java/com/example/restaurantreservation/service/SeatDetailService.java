package com.example.restaurantreservation.service;

import com.example.restaurantreservation.entity.SeatDetail;
import com.example.restaurantreservation.repository.SeatDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
public class SeatDetailService {

    private final SeatDetailRepository seatDetailRepository;

    public List<SeatDetail> getAllSeatDetailsByRestaurantId(Long restaurantId){
        return seatDetailRepository.findByRestaurantId(restaurantId);
    }

    public SeatDetail saveSeatDetail(SeatDetail seatDetail){
        return seatDetailRepository.save(seatDetail);
    }

    public void deleteSeatDetail(Long id){
        seatDetailRepository.deleteById(id);
    }

}
