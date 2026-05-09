package com.example.restaurantreservation.controller;

import com.example.restaurantreservation.form.RestaurantForm;
import com.example.restaurantreservation.service.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


/**
 * 管理者用レストラン関連のコントローラークラス
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminRestaurantController {

    private final RestaurantService restaurantService;

    /**
     * レストラン一覧画面の表示
     *
     * @param model Modelオブジェクト
     * @return レストラン一覧画面のテンプレートパス
     */
    @GetMapping("/restaurant-list")
    public String list(Model model) {
        // Modelにレストランの全件リストを追加
        model.addAttribute("restaurantList", restaurantService.getAllRestaurants());
        return "admin/restaurant-list";
    }

    /**
     * レストラン登録画面の表示
     *
     * @param model Modelオブジェクト
     * @return レストラン一覧画面のテンプレートパス
     */
    @GetMapping("/restaurant-input")
    public String input(Model model) {
        model.addAttribute("restaurantForm", new RestaurantForm());
        return "admin/restaurant-input";
    }

    /**
     * レストラン登録処理
     *
     * @param restaurantForm レストラン登録フォーム
     * @param result         バリデーション結果
     * @param model          Modelオブジェクト
     * @return 登録画面または登録完了画面のテンプレートパス
     */
    @PostMapping("/restaurant-input")
    public String inputPost(@Valid @ModelAttribute RestaurantForm restaurantForm, BindingResult result, Model model) {

        // バリデーションエラーの場合は再度一覧画面を表示
        if (result.hasErrors()) {
            return "admin/restaurant-input";
        }

        //　登録
        restaurantService.saveRestaurant(restaurantForm, null);
        return "redirect:/admin/restaurant-complete";

    }

    /**
     * レストラン編集画面の表示
     *
     * @param id    レストランID
     * @param model Modelオブジェクト
     * @return レストラン編集画面のテンプレートパス
     */
    @GetMapping("/restaurant-edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        RestaurantForm restaurantForm = new RestaurantForm();
        restaurantService.setRestaurantForm(id, restaurantForm);
        model.addAttribute("restaurantId", id);
        model.addAttribute("restaurantForm", restaurantForm);
        return "admin/restaurant-edit";
    }

    /**
     * レストラン更新処理
     *
     * @param id             レストランID
     * @param restaurantForm レストラン登録フォーム
     * @param result         バリデーション結果
     * @param model          Modelオブジェクト
     * @return 編集画面または登録完了画面のテンプレートパス
     */
    @PostMapping("/restaurant-edit/{id}")
    public String editPost(
            @PathVariable Long id,
            @Valid @ModelAttribute RestaurantForm restaurantForm,
            BindingResult result,
            Model model) {

        // バリデーションエラーの場合は再度一覧画面を表示
        if (result.hasErrors()) {
            return "admin/restaurant-edit";
        }

        //　更新
        restaurantService.saveRestaurant(restaurantForm, id);
        return "redirect:/admin/restaurant-complete";

    }

    /**
     * レストラン削除処理
     *
     * @param id    レストランID
     * @return 登録完了画面のテンプレートパス
     */
    @PostMapping("/restaurant-delete/{id}")
    public String deletePost(@PathVariable Long id) {
        restaurantService.deleteRestaurant(id);
        return "redirect:/admin/restaurant-complete";
    }

    /**
     * 登録完了画面の表示
     *
     * @return 登録完了画面のテンプレートパス
     */
    @GetMapping("/restaurant-complete")
    public String complete() {
        return "admin/restaurant-complete";
    }

}
