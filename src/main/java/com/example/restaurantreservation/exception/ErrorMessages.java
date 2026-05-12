package com.example.restaurantreservation.exception;

/**
 * 例外処理のエラーメッセージクラス
 */
public class ErrorMessages {
    private ErrorMessages() {}
    public static final String ERRORMESSAGE_403 = "権限エラーが発生しました。";
    public static final String ERRORMESSAGE_404 = "ページが見つかりません。";
    public static final String ERRORMESSAGE_500 = "サーバーエラーが発生しました。時間を空けて再度お試しください。";
}
