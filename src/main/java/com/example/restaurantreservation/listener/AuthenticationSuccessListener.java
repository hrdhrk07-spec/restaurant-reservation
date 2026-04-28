package com.example.restaurantreservation.listener;

import com.example.restaurantreservation.entity.User;
import com.example.restaurantreservation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 認証成功時のリスナークラス
 */
@Component
@RequiredArgsConstructor
public class AuthenticationSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {

    private final UserRepository userRepository;

    /**
     * 認証成功時に連続ログイン失敗回数を0にリセット
     *
     * @param event 認証成功イベント
     */
    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        // メールアドレスでユーザを検索
        Optional<User> optionalUser = userRepository.findByEmail(event.getAuthentication().getName());

        // ユーザが見つかったときだけ処理
        if (optionalUser.isPresent()) {

            // Optionalから値を取り出す
            User user = optionalUser.get();

            // 連続ログイン失敗回数に0をリセット
            user.setFailedLoginAttempts(0);

            // 登録
            userRepository.save(user);
        }
    }
}
