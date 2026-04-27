package com.example.restaurantreservation.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新規登録のフォームクラス
 */
@Data
public class RegisterForm {

    @NotBlank
    private String name;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 15, max = 64)
    private String password;

    @NotBlank
    private String passwordConfirm;
}
