package com.example.restaurantreservation.service;

import com.example.restaurantreservation.entity.Restaurant;
import com.example.restaurantreservation.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@Transactional
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public List<Restaurant> getAllRestaurants(){
        return restaurantRepository.findAll();
    }

    public Optional<Restaurant> getRestaurantById(Long id){
        return restaurantRepository.findById(id);
    }

    public Restaurant saveRestaurant(Restaurant restaurant){
        return restaurantRepository.save(restaurant);
    }

    public void deleteRestaurant(Long id){
        restaurantRepository.deleteById(id);
    }

}
