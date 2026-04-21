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

    /** ロール（0:一般ユーザー 1:管理者） */
    private int role;

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