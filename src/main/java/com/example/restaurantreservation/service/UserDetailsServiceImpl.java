package com.example.restaurantreservation.service;

import com.example.restaurantreservation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


/**
 * Spring Securityのログイン認証を担当するサービスクラス
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    /**
     * メールアドレスをもとにDBからユーザを取得する
     *
     * @param email メールアドレス
     * @return 認証に使用するユーザ情報
     * @throws UsernameNotFoundException ユーザが見つからない場合
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email + "was not found"));
    }

}
