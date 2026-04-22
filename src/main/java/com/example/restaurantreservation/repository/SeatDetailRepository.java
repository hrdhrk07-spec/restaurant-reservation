package com.example.restaurantreservation.repository;

import com.example.restaurantreservation.entity.SeatDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatDetailRepository extends JpaRepository<SeatDetail, Long>  {
    List<SeatDetail> findByRestaurantId(Long restaurantId);
}
