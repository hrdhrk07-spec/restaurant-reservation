package com.example.restaurantreservation.repository;

import com.example.restaurantreservation.entity.SeatDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SeatDetailRepository extends JpaRepository<SeatDetail, Long> {
    List<SeatDetail> findByRestaurantId(Long restaurantId);
    void deleteByRestaurantId(Long restaurantId);

    @Query("SELECT s "
            + "FROM SeatDetail s "
            + "WHERE s.restaurant.id = :restaurantId "
            + "AND s.personPerSeat >= :numberOfGuests ")
    List<SeatDetail> findAvailableSeatsByRestaurantAndGuests(
            @Param("restaurantId") Long restaurantId,
            @Param("numberOfGuests") int numberOfGuests
    );

}
