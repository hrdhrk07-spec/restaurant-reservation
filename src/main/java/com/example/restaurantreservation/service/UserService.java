package com.example.restaurantreservation.service;

import com.example.restaurantreservation.entity.User;
import com.example.restaurantreservation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * ユーザ関連のビジネスロジックを記載したサービスクラス
 */
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * ユーザの全件取得
     *
     * @return ユーザのリスト
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * IDに一致するユーザを1件取得
     *
     * @param id ユーザID
     * @return ユーザ
     */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * ユーザの登録
     *
     * @param user ユーザ
     * @return 登録したユーザ
     */
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    /**
     * IDに一致したユーザの削除
     *
     * @param id ユーザID
     */
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
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
