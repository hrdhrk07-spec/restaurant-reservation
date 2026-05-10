package com.example.restaurantreservation.exception;

/**
 * 存在しないページへのアクセスエラー(404)の例外クラス
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
