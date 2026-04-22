package com.example.restaurantreservation.repository;

import com.example.restaurantreservation.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
}
