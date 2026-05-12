package com.example.restaurantreservation.entity;

import com.example.restaurantreservation.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * ユーザ情報を管理するエンティティクラス
 */
@Data
@Entity
@Table(name = "users")
public class User extends BaseEntity implements UserDetails {

    /**
     * ID（主キー）
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * メールアドレス
     */
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * パスワード
     */
    private String password;

    /**
     * ロール （例：ADMIN / USER）
     */
    @Enumerated(EnumType.STRING)
    private UserRole role;

    /**
     * 氏名
     */
    private String name;

    /**
     * 電話番号
     */
    private String phoneNumber;

    /**
     * 連続ログイン失敗回数
     */
    private int failedLoginAttempts;

    /**
     * アカウントロック日時
     */
    private LocalDateTime lockTime;

    /**
     * このユーザに付与されている権限一覧を返す
     *
     * @return GrantedAuthorityを継承したクラスのコレクション
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    /**
     * ユーザ名（メールアドレス）を取得する
     */
    @Override
    public String getUsername() {
        return getEmail();
    }

    /**
     * アカウントが現在ロックされているかを判定する
     *
     * @return ロックされていなければtrue、されていればfalse
     */
    @Override
    public boolean isAccountNonLocked() {
        if (lockTime == null) return true;
        return Duration.between(getLockTime(), LocalDateTime.now()).toMinutes() >= 60;
    }
}
