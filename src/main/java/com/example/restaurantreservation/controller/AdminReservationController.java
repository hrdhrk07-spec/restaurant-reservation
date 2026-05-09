package com.example.restaurantreservation.controller;

import com.example.restaurantreservation.entity.Reservation;
import com.example.restaurantreservation.entity.ReservationStatus;
import com.example.restaurantreservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


/**
 * 管理者用予約管理のコントローラークラス
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminReservationController {

    private final ReservationService reservationService;

    /**
     * 予約管理一覧画面の表示
     *
     * @param model Modelオブジェクト
     * @return 予約管理一覧画面のテンプレートパス
     */
    @GetMapping("/reservation-list")
    public String list(Model model) {
        // Modelに予約の全件リストを追加
        model.addAttribute("reservationList", reservationService.getAllReservations());
        return "admin/reservation-list";
    }

    /**
     * 予約管理変更画面の表示
     *
     * @param id    予約ID
     * @param model Modelオブジェクト
     * @return レストラン編集画面のテンプレートパス
     */
    @GetMapping("/reservation-change/{id}")
    public String change(@PathVariable Long id, Model model) {
        Optional<Reservation> optionalReservation = reservationService.getReservationById(id);
        Reservation reservation = optionalReservation.orElseThrow();
        model.addAttribute("reservation", reservation);
        return "admin/reservation-change";
    }

    /**
     * 予約管理変更処理
     *
     * @param id     予約ID
     * @param status ステータス
     * @return 編集画面または登録完了画面のテンプレートパス
     */
    @PostMapping("/reservation-change/{id}")
    public String changePost(@PathVariable Long id, @RequestParam ReservationStatus status) {
        //　更新
        reservationService.changeReservationStatus(id, status);
        return "redirect:/admin/reservation-complete";

    }

    /**
     * 変更完了画面の表示
     *
     * @return 登録完了画面のテンプレートパス
     */
    @GetMapping("/reservation-complete")
    public String complete() {
        return "admin/reservation-complete";
    }

}
