package com.example.restaurantreservation.controller;

import com.example.restaurantreservation.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * ホーム画面のコントローラークラス
 */
@Controller
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    /**
     * レストラン一覧画面の表示
     *
     * @param location    所在地
     * @param cuisineType ジャンル
     * @param name        レストラン名
     * @return レストラン一覧画面のテンプレートパス
     */
    @GetMapping("/restaurant-list")
    public String home(@RequestParam(name = "location", required = false) String location,
                       @RequestParam(name = "cuisineType", required = false) String cuisineType,
                       @RequestParam(name = "name", required = false) String name,
                       Model model) {
        // Modelに値とレストランの検索結果を追加
        model.addAttribute("location", location);
        model.addAttribute("cuisineType", cuisineType);
        model.addAttribute("name", name);
        model.addAttribute("restaurantList",restaurantService.getRestaurants(location, cuisineType, name));
        return "user/restaurant-list";
    }

}
