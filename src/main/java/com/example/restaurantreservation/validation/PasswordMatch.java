package com.example.restaurantreservation.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * パスワードと確認用パスワードの一致を検証するアノテーション
 */
@Target({ElementType.TYPE}) // アノテーションをつけられる場所を指定
@Retention(RetentionPolicy.RUNTIME) // アノテーション情報が維持される範囲
@Constraint(validatedBy = PasswordMatchValidator.class) // 実行クラス
public @interface PasswordMatch {
    // デフォルトのエラーメッセージ
    String message() default "パスワードが一致しません";

    // バリデーショングループ
    Class<?>[] groups() default {};

    // バリデーションペイロード
    Class<? extends Payload>[] payload() default {};
}
