package com.example.restaurantreservation.service;

import com.example.restaurantreservation.entity.Holiday;
import com.example.restaurantreservation.entity.Restaurant;
import com.example.restaurantreservation.entity.SeatDetail;
import com.example.restaurantreservation.enums.HolidayDayOfWeek;
import com.example.restaurantreservation.exception.ResourceNotFoundException;
import com.example.restaurantreservation.form.RestaurantForm;
import com.example.restaurantreservation.form.SeatDetailForm;
import com.example.restaurantreservation.repository.HolidayRepository;
import com.example.restaurantreservation.repository.ReservationRepository;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * レストラン関連のビジネスロジックを記載したサービスクラス
 */
@Service
@Transactional
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final SeatDetailRepository seatDetailRepository;
    private final ReservationRepository reservationRepository;
    private final HolidayRepository holidayRepository;

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
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("レストラン取得の失敗 ID:" + id));
    }

    /**
     * レストラン登録フォームに値をセット
     *
     * @param id             レストランID
     * @param restaurantForm レストラン登録フォーム
     */
    public void setRestaurantForm(Long id, RestaurantForm restaurantForm) {
        // IDからレストラン情報を取得
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("レストラン取得の失敗 ID:" + id));

        // レストラン登録フォームに値をセット
        restaurantForm.setName(restaurant.getName());
        restaurantForm.setCuisineType(restaurant.getCuisineType());
        restaurantForm.setLocation(restaurant.getLocation());
        restaurantForm.setImagePath(restaurant.getImagePath());
        restaurantForm.setReceptionStartTime(restaurant.getReceptionStartTime());
        restaurantForm.setReceptionEndTime(restaurant.getReceptionEndTime());

        // 定休日を取得してレストラン登録フォームにセット
        restaurantForm.setHolidayDayOfWeeks(
                restaurant.getHolidays().stream()
                        .map(Holiday::getHolidayDayOfWeek)
                        .collect(Collectors.toList())
        );

        // レストランIDから席詳細を取得
        List<SeatDetail> seatDetails = seatDetailRepository.findByRestaurantId(id);

        // 席詳細フォームのリストを作成
        List<SeatDetailForm> seatDetailForms = new ArrayList<>();

        // 席詳細の数だけループ
        for (SeatDetail seatDetail : seatDetails) {

            // 席詳細フォームに値をセット
            SeatDetailForm seatDetailForm = new SeatDetailForm();
            seatDetailForm.setId(seatDetail.getId());
            seatDetailForm.setPersonPerSeat(seatDetail.getPersonPerSeat());
            seatDetailForm.setNumberOfSeats(seatDetail.getNumberOfSeats());
            seatDetailForm.setDuration(seatDetail.getDuration());

            // リストにセット
            seatDetailForms.add(seatDetailForm);

        }

        // レストラン登録フォームに席詳細フォームをセット
        restaurantForm.setSeatDetails(seatDetailForms);

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
        restaurant.setReceptionStartTime(restaurantForm.getReceptionStartTime());
        restaurant.setReceptionEndTime(restaurantForm.getReceptionEndTime());
        restaurant.setId(id);

        // レストランを登録
        restaurant = restaurantRepository.save(restaurant);

        // 既存の定休日を削除
        holidayRepository.deleteByRestaurantId(restaurant.getId());

        // フォームからHolidayエンティティに値を設定
        if (restaurantForm.getHolidayDayOfWeeks() != null) {
            for (HolidayDayOfWeek h : restaurantForm.getHolidayDayOfWeeks()) {
                Holiday holiday = new Holiday();
                holiday.setRestaurant(restaurant);
                holiday.setHolidayDayOfWeek(h);
                holidayRepository.save(holiday);
            }
        }

        // 席詳細フォームを取得
        List<SeatDetailForm> seatDetailForms = restaurantForm.getSeatDetails();

        // 更新時は席詳細の削除チェックを実施
        if (id != null) {

            // 既存の席詳細IDリストを取得
            List<Long> seatDetailIds = seatDetailRepository.findByRestaurantId(restaurant.getId())
                    .stream()
                    .map(SeatDetail::getId)
                    .collect(Collectors.toList());

            // 席詳細フォームのIDリストを取得
            List<Long> formSeatDetailIds = seatDetailForms
                    .stream()
                    .map(SeatDetailForm::getId)
                    .collect(Collectors.toList());

            //  席詳細フォームのIDリストからnullを除去（追加された席詳細の分を取り除く）
            formSeatDetailIds.removeAll(Collections.singleton(null));

            // 削除対象の特定
            seatDetailIds.removeAll(formSeatDetailIds);

            // 削除対象ごとに予約の存在チェック
            for (Long seatDetailId : seatDetailIds) {
                // 予約が存在しない場合は削除
                if (reservationRepository.countBySeatDetailId(seatDetailId) == 0) {
                    seatDetailRepository.deleteById(seatDetailId);
                }
            }
        }

        // 席詳細の数だけループ
        for (SeatDetailForm seatDetailForm : seatDetailForms) {

            // フォームからSeatDetailエンティティに値を設定
            SeatDetail seatDetail = new SeatDetail();
            seatDetail.setRestaurant(restaurant);
            seatDetail.setPersonPerSeat(seatDetailForm.getPersonPerSeat());
            seatDetail.setNumberOfSeats(seatDetailForm.getNumberOfSeats());
            seatDetail.setDuration(seatDetailForm.getDuration());
            seatDetail.setId(seatDetailForm.getId());

            // 登録
            seatDetailRepository.save(seatDetail);
        }

    }

    /**
     * IDに一致したレストランの削除
     *
     * @param id レストランID
     */
    public void deleteRestaurant(Long id) {
        reservationRepository.deleteByRestaurantId(id);
        seatDetailRepository.deleteByRestaurantId(id);
        holidayRepository.deleteByRestaurantId(id);
        restaurantRepository.deleteById(id);
    }

}
