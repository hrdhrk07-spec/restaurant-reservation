package com.example.restaurantreservation.service;

import com.example.restaurantreservation.entity.Restaurant;
import com.example.restaurantreservation.entity.SeatDetail;
import com.example.restaurantreservation.form.RestaurantForm;
import com.example.restaurantreservation.form.SeatDetailForm;
import com.example.restaurantreservation.repository.RestaurantRepository;
import com.example.restaurantreservation.repository.SeatDetailRepository;
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
    private final SeatDetailRepository seatDetailRepository;

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
    public Restaurant getRestaurantById(Long id) {
        return restaurantRepository.findById(id).orElseThrow();
    }

    /**
     * レストラン登録フォームに値をセット
     *
     * @param id             レストランID
     * @param restaurantForm レストラン登録フォーム
     */
    public void setRestaurantForm(Long id, RestaurantForm restaurantForm) {
        // IDからレストラン情報を取得
        Restaurant restaurant = restaurantRepository.findById(id).orElseThrow();

        // レストラン登録フォームに値をセット
        restaurantForm.setName(restaurant.getName());
        restaurantForm.setCuisineType(restaurant.getCuisineType());
        restaurantForm.setLocation(restaurant.getLocation());
        restaurantForm.setImagePath(restaurant.getImagePath());
        restaurantForm.setHolidays(restaurant.getHolidays());
        restaurantForm.setReceptionStartTime(restaurant.getReceptionStartTime());
        restaurantForm.setReceptionEndTime(restaurant.getReceptionEndTime());

        // レストランIDから席詳細情報を取得
        // 一旦リストの最初の席詳細だけを扱う
        SeatDetail seatDetail = seatDetailRepository.findByRestaurantId(id).getFirst();

        // 席詳細フォームに値をセット
        SeatDetailForm seatDetailForm = new SeatDetailForm();
        seatDetailForm.setId(seatDetail.getId());
        seatDetailForm.setPersonPerSeat(seatDetail.getPersonPerSeat());
        seatDetailForm.setNumberOfSeats(seatDetail.getNumberOfSeats());
        seatDetailForm.setDuration(seatDetail.getDuration());

        // レストラン登録フォームに席詳細フォームをセット
        restaurantForm.setSeatDetail(seatDetailForm);

    }

    /**
     * レストランの登録
     *
     * @param restaurantForm レストラン登録フォーム
     * @param id             レストランID
     */
    public void saveRestaurant(RestaurantForm restaurantForm, Long id) {

        // フォームからRestaurantエンティティに値を設定
        Restaurant restaurant = new Restaurant();
        restaurant.setName(restaurantForm.getName());
        restaurant.setCuisineType(restaurantForm.getCuisineType());
        restaurant.setLocation(restaurantForm.getLocation());
        restaurant.setImagePath(restaurantForm.getImagePath());
        restaurant.setHolidays(restaurantForm.getHolidays());
        restaurant.setReceptionStartTime(restaurantForm.getReceptionStartTime());
        restaurant.setReceptionEndTime(restaurantForm.getReceptionEndTime());
        restaurant.setId(id);

        // レストランを登録
        restaurant = restaurantRepository.save(restaurant);

        // フォームからSeatDetailエンティティに値を設定
        SeatDetail seatDetail = new SeatDetail();
        seatDetail.setRestaurant(restaurant);
        seatDetail.setPersonPerSeat(restaurantForm.getSeatDetail().getPersonPerSeat());
        seatDetail.setNumberOfSeats(restaurantForm.getSeatDetail().getNumberOfSeats());
        seatDetail.setDuration(restaurantForm.getSeatDetail().getDuration());
        seatDetail.setId(restaurantForm.getSeatDetail().getId());

        // 登録
        seatDetailRepository.save(seatDetail);

    }

    /**
     * IDに一致したレストランの削除
     *
     * @param id レストランID
     */
    public void deleteRestaurant(Long id) {
        seatDetailRepository.deleteByRestaurantId(id);
        restaurantRepository.deleteById(id);
    }

}
