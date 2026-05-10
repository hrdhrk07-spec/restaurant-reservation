package com.example.restaurantreservation.service;

import com.example.restaurantreservation.entity.User;
import com.example.restaurantreservation.entity.UserRole;
import com.example.restaurantreservation.form.RegisterForm;
import com.example.restaurantreservation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * ユーザ関連のビジネスロジックを記載したサービスクラス
 */
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * メールアドレスに一致するユーザを1件取得（認証用）
     *
     * @param email メールアドレス
     * @return ユーザ
     */
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * メールアドレスに一致するユーザを1件取得
     *
     * @param email メールアドレス
     * @return ユーザ
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow();
    }

    /**
     * ユーザの登録
     *
     * @param registerForm ユーザ登録フォーム
     */
    public void saveUser(RegisterForm registerForm) {

        // フォームからUserエンティティに値を設定
        User user = new User();
        user.setName(registerForm.getName());
        user.setPhoneNumber(registerForm.getPhoneNumber());
        user.setEmail(registerForm.getEmail().toLowerCase());   // 小文字変換
        user.setPassword(registerForm.getPassword());

        // パスワードの暗号化
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // ロールの設定
        user.setRole(UserRole.USER);

        // 登録
        userRepository.save(user);

    }

    /**
     * ユーザの登録
     *
     * @param user ユーザ
     */
    public void saveUser(User user) {
        userRepository.save(user);
    }

    /**
     * Emailに一致したユーザを取得
     *
     * @param email メールアドレス
     * @return 一致したユーザが存在すればtrue、存在しなければfalse
     */
    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

}
