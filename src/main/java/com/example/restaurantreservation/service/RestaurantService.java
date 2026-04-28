package com.example.restaurantreservation.service;

import com.example.restaurantreservation.entity.Restaurant;
import com.example.restaurantreservation.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * レストラン関連のビジネスロジックを記載したサービスクラス
 */
@Service
@Transactional
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    /**
     * レストランの全件取得
     *
     * @return レストランのリスト
     */
    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    /**
     * 最新10件のレストランを取得
     *
     * @return 最新10件のレストランのリスト
     */
    public List<Restaurant> getNewTenRestaurants() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        return restaurantRepository.findAll(pageable).getContent();
    }

    /**
     * IDに一致するレストランを1件取得
     *
     * @param id レストランID
     * @return レストラン
     */
    public Optional<Restaurant> getRestaurantById(Long id) {
        return restaurantRepository.findById(id);
    }

    /**
     * レストランの登録
     *
     * @param restaurant レストラン
     * @return 登録したレストラン
     */
    public Restaurant saveRestaurant(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    /**
     * IDに一致したレストランの削除
     *
     * @param id レストランID
     */
    public void deleteRestaurant(Long id) {
        restaurantRepository.deleteById(id);
    }

}
