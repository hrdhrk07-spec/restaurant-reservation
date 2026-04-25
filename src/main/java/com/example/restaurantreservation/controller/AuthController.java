package com.example.restaurantreservation.controller;

import com.example.restaurantreservation.entity.User;
import com.example.restaurantreservation.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    // ログイン画面の表示
    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    // 新規登録画面の表示
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    // 新規登録処理
    @PostMapping("/register")
    public String registerPost(Model model, @Valid @ModelAttribute User user, BindingResult result) {

        // バリデーションエラーの場合は再度登録画面を表示
        if (result.hasErrors()) {
            return "auth/register";
        }

        // メールアドレスの小文字変換
        user.setEmail(user.getEmail().toLowerCase());

        // 登録済みのメールアドレスの場合は再度登録画面を表示
        if (userService.existsByEmail(user.getEmail())) {
            model.addAttribute("errorMessage", "このメールアドレスは既に登録されています");
            return "auth/register";
        }

        // パスワードの暗号化
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        //　登録
        userService.saveUser(user);
        return "redirect:/auth/register-complete";
    }

    // 登録完了画面の表示
    @GetMapping("/register/complete")
    public String complete() {
        return "auth/register-complete";
    }

}
