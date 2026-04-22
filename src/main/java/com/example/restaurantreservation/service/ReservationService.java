package com.example.restaurantreservation.service;

import com.example.restaurantreservation.entity.Reservation;
import com.example.restaurantreservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public List<Reservation> getAllReservations(){
        return reservationRepository.findAll();
    }

    public List<Reservation> getReservationsByUserId(Long userId){
        return reservationRepository.findByUserId(userId);
    }

    public Reservation saveReservation(Reservation reservation){
        return reservationRepository.save(reservation);
    }

}
