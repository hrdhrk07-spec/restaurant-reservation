package com.example.restaurantreservation.controller;

import com.example.restaurantreservation.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * ホーム画面のコントローラークラス
 */
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final RestaurantService restaurantService;

    /**
     * ホーム画面の表示
     *
     * @return ホーム画面のテンプレートパス
     */
    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("restaurantList", restaurantService.getNewTenRestaurants());
        model.addAttribute("isHome", true);
        return "user/home";
    }

}
