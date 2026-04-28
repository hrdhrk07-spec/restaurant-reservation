package com.example.restaurantreservation.listener;

import com.example.restaurantreservation.entity.User;
import com.example.restaurantreservation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 認証失敗時のリスナークラス
 */
@Component
@RequiredArgsConstructor
public class AuthenticationFailureListener implements ApplicationListener<AbstractAuthenticationFailureEvent> {

    private final UserRepository userRepository;

    /**
     * 認証失敗時に連続ログイン失敗回数とロック日時を更新
     *
     * @param event 認証失敗イベント
     */
    @Override
    public void onApplicationEvent(AbstractAuthenticationFailureEvent event) {
        // メールアドレスでユーザを検索
        Optional<User> optionalUser = userRepository.findByEmail(event.getAuthentication().getName());

        // ユーザが見つかったときだけ処理
        if (optionalUser.isPresent()) {

            // Optionalから値を取り出す
            User user = optionalUser.get();

            // 連続ログイン失敗回数に1をプラス
            int failedLoginAttempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(failedLoginAttempts);

            // 連続ログイン失敗回数が10以上の時ロック日時をセット
            if (failedLoginAttempts >= 10) {
                user.setLockTime(LocalDateTime.now());
            }

            // 登録
            userRepository.save(user);
        }
    }
}
