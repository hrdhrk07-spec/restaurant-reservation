package com.example.restaurantreservation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Securityの設定を記載したコンフィグクラス
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 認証に関する設定
     *
     * @param http HTTPセキュリティ設定
     * @return セキュリティーフィルターチェーン
     * @throws Exception セキュリティ設定に失敗した場合
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // login、registerは全員がアクセスできる
                        .requestMatchers("/login", "/register", "/register/**").permitAll()

                        // adminはADMIN権限のみがアクセスできる
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // 他のリンクは全て認証が必要である
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        // ログインのURL
                        .loginPage("/login")

                        // メールアドレスで認証するよう設定
                        .usernameParameter("email")

                        // ログイン成功時のリダイレクト先
                        .defaultSuccessUrl("/home", true) // trueにすることで常に/homeにリダイレクト

                        // ログイン失敗時のリダイレクト先
                        .failureUrl("/login?error")
                )
                .logout(logout -> logout
                        // ログアウトのURL
                        .logoutUrl("/logout")

                        // ログアウト成功時のリダイレクト先
                        .logoutSuccessUrl("/login")
                );
        return http.build();
    }

    /**
     * パスワードをBCryptで暗号化するエンコーダーを返す
     *
     * @return パスワードエンコーダー
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
