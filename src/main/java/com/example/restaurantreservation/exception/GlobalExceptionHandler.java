package com.example.restaurantreservation.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 例外処理のハンドラクラス
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 403エラー
     *
     * @param ex    例外
     * @param model Modelオブジェクト
     * @return エラー画面のテンプレートパス
     */
    @ExceptionHandler(UnauthorizedAccessException.class)
    public String handleForbidden(Exception ex, Model model) {
        log.warn("権限エラー：{}", ex.getMessage(), ex);
        model.addAttribute("error", ErrorMessages.ERRORMESSAGE_403);
        return "error/403";
    }

    /**
     * 404エラー
     *
     * @param ex    例外
     * @param model Modelオブジェクト
     * @return エラー画面のテンプレートパス
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleNotFound(Exception ex, Model model) {
        log.warn("存在エラー：{}", ex.getMessage(), ex);
        model.addAttribute("error", ErrorMessages.ERRORMESSAGE_404);
        return "error/404";
    }

    /**
     * 500エラー
     *
     * @param ex    例外
     * @param model Modelオブジェクト
     * @return エラー画面のテンプレートパス
     */
    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, Model model) {
        log.error("サーバーエラー：{}", ex.getMessage(), ex);
        model.addAttribute("error", ErrorMessages.ERRORMESSAGE_500);
        return "error/500";
    }
}
