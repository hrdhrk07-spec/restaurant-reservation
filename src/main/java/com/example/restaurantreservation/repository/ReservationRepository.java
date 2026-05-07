package com.example.restaurantreservation.repository;

import com.example.restaurantreservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserIdOrderByReservedAtDesc(Long userId);

    @Query(value = "SELECT COUNT(*) "
            + "FROM reservations r "
            + "INNER JOIN seat_details s ON r.seat_detail_id = s.id "
            + "WHERE r.seat_detail_id = :seatDetailId "
            + "AND r.reserved_at < :endTime "
            + "AND r.reserved_at + (s.duration * INTERVAL '1 minute') > :reservedAt "
            + "AND r.status IN :statuses ",
            nativeQuery = true)
    int countOverlapping(
            @Param("seatDetailId") Long seatDetailId,
            @Param("reservedAt") LocalDateTime reservedAt,
            @Param("endTime") LocalDateTime endTime,
            @Param("statuses") List<String> statuses
    );
}
