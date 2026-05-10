package com.example.restaurantreservation.controller;

import com.example.restaurantreservation.form.RegisterForm;
import com.example.restaurantreservation.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 認証周りのコントローラークラス
 */
@Controller
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    /**
     * ログイン画面の表示
     *
     * @return ログイン画面のテンプレートパス
     */
    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    /**
     * 新規登録画面の表示
     *
     * @param model Modelオブジェクト
     * @return 新規登録画面のテンプレートパス
     */
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("registerForm", new RegisterForm());
        return "auth/register";
    }

    /**
     * 新規登録処理
     *
     * @param registerForm 新規登録フォーム
     * @param result バリデーション結果
     * @param model Modelオブジェクト
     * @return 登録画面または登録完了画面のテンプレートパス
     */
    @PostMapping("/register")
    public String registerPost(@Valid @ModelAttribute RegisterForm registerForm, BindingResult result, Model model) {

        // バリデーションエラーの場合は再度登録画面を表示
        if (result.hasErrors()) {
            return "auth/register";
        }

        // 登録済みのメールアドレスの場合は再度登録画面を表示
        if (userService.existsByEmail(registerForm.getEmail().toLowerCase())) {
            model.addAttribute("errorMessage", "このメールアドレスは既に登録されています");
            return "auth/register";
        }

        //　登録
        userService.saveUser(registerForm);
        return "redirect:/register/complete";

    }

    /**
     * 登録完了画面の表示
     *
     * @return 登録完了画面のテンプレートパス
     */
    @GetMapping("/register/complete")
    public String complete() {
        return "auth/register-complete";
    }

}
