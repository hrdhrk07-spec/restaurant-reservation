package com.example.restaurantreservation.form;

import com.example.restaurantreservation.validation.PasswordMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新規登録のフォームクラス
 */
@PasswordMatch
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
    @Size(min = 15)
    private String password;

    @NotBlank
    @Size(min = 15)
    private String passwordConfirm;
}
