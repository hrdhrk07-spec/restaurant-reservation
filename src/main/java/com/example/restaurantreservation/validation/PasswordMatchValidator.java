package com.example.restaurantreservation.validation;

import com.example.restaurantreservation.form.RegisterForm;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * パスワードが確認用と一致しているかを確認するバリデータクラス
 */
public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, RegisterForm> {

    /**
     * パスワード確認
     *
     * @param form 新規登録フォーム
     * @param context バリデーションコンテキスト
     * @return パスワードが一致する場合はtrue、一致しない場合はfalse
     */
    @Override
    public boolean isValid(RegisterForm form, ConstraintValidatorContext context) {
        return form.getPassword().equals(form.getPasswordConfirm());
    }
}
