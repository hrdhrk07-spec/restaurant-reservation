package com.example.restaurantreservation.controller;

import com.example.restaurantreservation.entity.Restaurant;
import com.example.restaurantreservation.entity.SeatDetail;
import com.example.restaurantreservation.form.ReservationForm;
import com.example.restaurantreservation.service.ReservationService;
import com.example.restaurantreservation.service.RestaurantService;
import com.example.restaurantreservation.service.SeatDetailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * 予約関連のコントローラークラス
 */
@Controller
@SessionAttributes("reservationForm")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final RestaurantService restaurantService;
    private final SeatDetailService seatDetailService;

    /**
     * レストランの取得及びmodelへの追加
     *
     * @param id    レストランID
     * @param model Modelオブジェクト
     * @return レストラン一覧画面のテンプレートパス
     */
    private String getRestaurantSetModel(Long id, Model model) {
        // idからレストランを取得
        Optional<Restaurant> optionalRestaurant = restaurantService.getRestaurantById(id);

        // 取得できたときのみmodelに値を追加
        if (optionalRestaurant.isPresent()) {
            Restaurant restaurant = optionalRestaurant.get();
            model.addAttribute("restaurant", restaurant);
            return null;
        } else {
            return "redirect:/restaurant-list";
        }

    }

    /**
     * 予約入力画面の表示
     *
     * @param id    レストランid
     * @param model Modelオブジェクト
     * @return レストラン一覧画面のテンプレートパス
     */
    @GetMapping("/reservation-input/{id}")
    public String input(@PathVariable("id") Long id, Model model) {
        model.addAttribute("reservationForm", new ReservationForm());

        // IDからレストランを取得
        String redirect = getRestaurantSetModel(id, model);

        // レストランが取得できなかった場合はリダイレクト
        if (redirect != null) {
            return redirect;
        } else {
            return "user/reservation-input";
        }

    }

    /**
     * 予約入力画面で空席を表示
     *
     * @param id              レストランID
     * @param reservationForm 予約フォーム
     * @param result          バリデーション結果
     * @param model           Modelオブジェクト
     * @return レストラン一覧画面のテンプレートパス
     */
    @PostMapping("/reservation-input/{id}")
    public String inputPost(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute ReservationForm reservationForm,
            BindingResult result,
            Model model) {

        // IDからレストランを取得
        String redirect = getRestaurantSetModel(id, model);

        // レストランが取得できなかった場合はリダイレクト
        if (redirect != null) {
            return redirect;
        }

        // バリデーションエラーの場合は再度入力画面を表示
        if (result.hasErrors()) {
            return "user/reservation-input";
        }

        model.addAttribute("reservationForm", reservationForm);
        // 予約可能な席詳細を取得して画面に渡すようセット
        model.addAttribute("seatDetailList",
                reservationService.getAvailableSeats(id, reservationForm.getReservedAt(), reservationForm.getNumberOfGuests()));
        return "user/reservation-input";

    }

    /**
     * 予約確認画面の表示
     *
     * @param seatDetailId      席詳細ID
     * @param reservationForm   予約フォーム
     * @param model             Modelオブジェクト
     * @return レストラン一覧画面のテンプレートパス
     */
    @PostMapping("/reservation-confirm")
    public String confirmPost(@RequestParam(required = false) Long seatDetailId, @ModelAttribute ReservationForm reservationForm, Model model) {
        //　席詳細IDを指定していなかった場合は再度予約入力画面を表示
        if(seatDetailId == null){
            return "user/reservation-input";
        }

        // 席詳細を取得
        Optional<SeatDetail> optionalSeatDetail = seatDetailService.getSeatDetailById(seatDetailId);

        // 取得できたときのみmodelに値を追加
        if (optionalSeatDetail.isPresent()) {

            // 席詳細IDからレストラン情報を取得
            SeatDetail seatDetail = optionalSeatDetail.get();
            Restaurant restaurant = seatDetail.getRestaurant();

            // 必要な情報を画面に渡すようセット
            model.addAttribute("restaurant", restaurant);
            model.addAttribute("seatDetail", seatDetail);
            model.addAttribute("reservationForm", reservationForm);

            return "user/reservation-confirm";
        } else {
            return "redirect:/restaurant-list";
        }

    }

}
