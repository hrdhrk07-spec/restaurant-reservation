package com.example.restaurantreservation.controller;

import com.example.restaurantreservation.entity.*;
import com.example.restaurantreservation.exception.ResourceNotFoundException;
import com.example.restaurantreservation.exception.UnauthorizedAccessException;
import com.example.restaurantreservation.form.ReservationForm;
import com.example.restaurantreservation.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

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
    private final UserService userService;

    /**
     * 予約入力画面の表示
     *
     * @param id    レストランid
     * @param model Modelオブジェクト
     * @return レストラン一覧画面のテンプレートパス
     */
    @GetMapping("/reservation-input/{id}")
    public String input(@PathVariable("id") Long id, Model model) {
        // IDからレストランを取得
        Restaurant restaurant = restaurantService.getRestaurantById(id);

        model.addAttribute("restaurant", restaurant);
        model.addAttribute("reservationForm", new ReservationForm());
        return "user/reservation-input";
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
        Restaurant restaurant = restaurantService.getRestaurantById(id);
        model.addAttribute("restaurant", restaurant);

        // バリデーションエラーの場合は再度入力画面を表示
        if (result.hasErrors()) {
            return "user/reservation-input";
        }

        // 定休日の場合は再度入力画面を表示
        if (reservationService.isHoliday(id, reservationForm.getReservedAt())) {
            model.addAttribute("holidayError", "選択した日は定休日です。");
            return "user/reservation-input";
        }

        // 予約時刻が受付時間外の場合は再度入力画面を表示
        if (!ReservationService.canReception(
                reservationForm.getReservedAt(),
                restaurant.getReceptionStartTime(),
                restaurant.getReceptionEndTime())) {
            model.addAttribute("receptionError", "選択した時刻は受付時間外です。");
            return "user/reservation-input";
        }

        // 予約可能な席詳細を取得して画面に渡すようセット
        model.addAttribute("seatDetailList",
                reservationService.getAvailableSeats(id, reservationForm.getReservedAt(), reservationForm.getNumberOfGuests()));
        model.addAttribute("reservationForm", reservationForm);
        return "user/reservation-input";

    }

    /**
     * 予約確認画面の表示
     *
     * @param seatDetailId    席詳細ID
     * @param reservationForm 予約フォーム
     * @param model           Modelオブジェクト
     * @return レストラン一覧画面のテンプレートパス
     */
    @PostMapping("/reservation-confirm")
    public String confirmPost(@RequestParam(required = false) Long seatDetailId, @ModelAttribute ReservationForm reservationForm, Model model) {

        //　席詳細IDが空の場合はエラー
        if (seatDetailId == null) {
            throw new ResourceNotFoundException("席詳細ID取得の失敗");
        }

        // 席詳細IDからレストラン情報を取得
        SeatDetail seatDetail = seatDetailService.getSeatDetailById(seatDetailId);
        Restaurant restaurant = seatDetail.getRestaurant();

        // 必要な情報を画面に渡すようセット
        model.addAttribute("restaurant", restaurant);
        model.addAttribute("seatDetail", seatDetail);
        model.addAttribute("reservationForm", reservationForm);

        return "user/reservation-confirm";

    }

    /**
     * 予約の登録
     *
     * @param userDetails     ユーザ情報
     * @param seatDetailId    席詳細ID
     * @param reservationForm 予約フォーム
     * @param model           Modelオブジェクト
     * @return 予約完了画面のテンプレートパス
     */
    @PostMapping("/reservation-complete")
    public String completePost(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long seatDetailId,
            @ModelAttribute ReservationForm reservationForm,
            Model model) {

        // ログインユーザからメールアドレスを取得
        String email = userDetails.getUsername();

        // ユーザと席詳細をセット
        User user = userService.getUserByEmail(email);
        SeatDetail seatDetail = seatDetailService.getSeatDetailById(seatDetailId);

        // 予約情報を登録し、画面に渡すようセット
        model.addAttribute("reservation", reservationService.saveReservation(user, seatDetail, reservationForm));

        return "user/reservation-complete";

    }

    /**
     * 予約一覧画面の表示
     *
     * @param userDetails ユーザ情報
     * @param model       Modelオブジェクト
     * @return レストラン一覧画面のテンプレートパス
     */
    @GetMapping("/reservation-list")
    public String list(@AuthenticationPrincipal UserDetails userDetails, Model model) {

        // ログインユーザからメールアドレスを取得
        String email = userDetails.getUsername();

        // ユーザをセット
        User user = userService.getUserByEmail(email);

        // ユーザの予約情報を取得してmodelにセット
        model.addAttribute("reservationList", reservationService.getReservationsByUserId(user.getId()));

        return "user/reservation-list";

    }

    /**
     * 予約が現在のユーザのものであるかをチェック
     *
     * @param email       メールアドレス
     * @param reservation 予約
     * @return 予約が現在のユーザのものであればTrue、そうでなければFalse
     */
    private Boolean shouldCancel(String email, Reservation reservation) {

        // ユーザをセット
        User user = userService.getUserByEmail(email);

        // 比較
        return Objects.equals(reservation.getUser().getId(), user.getId());

    }

    /**
     * キャンセル確認画面の表示
     *
     * @param userDetails ユーザ情報
     * @param id          パスから取得したid
     * @param model       Modelオブジェクト
     * @return キャンセル確認画面のテンプレートパス
     */
    @GetMapping("/cancel-confirm/{id}")
    public String cancelConfirm(@AuthenticationPrincipal UserDetails userDetails, @PathVariable("id") Long id, Model model) {

        // 予約情報の取得
        Reservation reservation = reservationService.getReservationById(id);

        // 取得した予約情報が現在のユーザのものであるかをチェック
        if (shouldCancel(userDetails.getUsername(), reservation)) {
            model.addAttribute("reservation", reservation);
            return "user/cancel-confirm";
        } else {
            throw new UnauthorizedAccessException("予約ステータス更新の失敗 ユーザ：" + userDetails.getUsername() + " 予約ID：" + id);
        }

    }

    /**
     * キャンセル完了画面の表示
     *
     * @param userDetails ユーザ情報
     * @param id          予約ID
     * @return レストラン一覧画面のテンプレートパス
     */
    @PostMapping("/cancel-complete")
    public String cancelPost(@AuthenticationPrincipal UserDetails userDetails, @RequestParam("id") Long id) {

        // 予約情報の取得
        Reservation reservation = reservationService.getReservationById(id);

        // 取得した予約情報が現在のユーザのものであるかをチェック
        if (shouldCancel(userDetails.getUsername(), reservation)) {
            reservationService.cancelReservation(reservation);
            return "user/cancel-complete";
        } else {
            throw new UnauthorizedAccessException("予約ステータス更新の失敗 ユーザ：" + userDetails.getUsername() + " 予約ID：" + id);
        }

    }

}
