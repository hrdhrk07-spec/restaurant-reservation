package com.example.restaurantreservation.repository;

import com.example.restaurantreservation.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
