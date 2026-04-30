package com.example.restaurantreservation.service;

import com.example.restaurantreservation.entity.Restaurant;
import com.example.restaurantreservation.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
     * 検索条件に一致するレストランを取得
     *
     * @param location    所在地
     * @param cuisineType ジャンル
     * @param name        レストラン名
     * @return レストランのリスト
     */
    public List<Restaurant> getRestaurants(String location, String cuisineType, String name) {
        // 所在地の条件
        Specification<Restaurant> locationSpec = (root, query, cb) ->
                StringUtils.hasText(location) ? cb.like(root.get("location"), "%" + location + "%") : null;

        // ジャンルの条件
        Specification<Restaurant> cuisineTypeSpec = (root, query, cb) ->
                StringUtils.hasText(cuisineType) ? cb.equal(root.get("cuisineType"), cuisineType) : null;

        // レストラン名の条件
        Specification<Restaurant> nameSpec = (root, query, cb) ->
                StringUtils.hasText(name) ? cb.like(root.get("name"), "%" + name + "%") : null;

        // 条件の組み立て
        Specification<Restaurant> spec = Specification
                .<Restaurant>unrestricted()
                .and(locationSpec)
                .and(cuisineTypeSpec)
                .and(nameSpec);

        return restaurantRepository.findAll(spec);
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
