package com.example.restaurantreservation.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * ユーザ情報を管理するエンティティクラス
 */
@Data
@Entity
@Table(name = "users")
public class User {

    /** ID（主キー） */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** ロール （例：ADMIN / USER）*/
    @Enumerated(EnumType.STRING)
    private UserRole role;

    /** 氏名 */
    private String name;

    /** 電話番号 */
    private String phoneNumber;

    /** メールアドレス */
    private String email;

    /** パスワード */
    private String password;

    /** 作成日時 */
    private LocalDateTime createdAt;

    /** 更新日時 */
    private LocalDateTime updatedAt;
}
