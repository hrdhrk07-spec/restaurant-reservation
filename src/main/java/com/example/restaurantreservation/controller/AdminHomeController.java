package com.example.restaurantreservation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 管理者用ホーム画面のコントローラークラス
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminHomeController {

    /**
     * ホーム画面の表示
     *
     * @return ホーム画面のテンプレートパス
     */
    @GetMapping("/home")
    public String home() {
        return "admin/home";
    }

}
