package com.example.restaurantreservation.exception;

/**
 * 権限エラー(403)の例外クラス
 */
public class UnauthorizedAccessException extends RuntimeException {
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
