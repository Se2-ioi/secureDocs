package com.securedoc.domain.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String userId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 20)
    private String username;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column(length = 20)
    private String phoneNum;

    @Column
    private int accountStatus = 1;

    @Enumerated(EnumType.STRING)
    private Role role;

    /* 생성일자, 변경일자 자동 생성 */
    @Builder.Default
    private LocalDateTime createDate = LocalDateTime.now();
    private LocalDateTime updateDate;

    @PreUpdate
    protected void onUpdate() {
        updateDate = LocalDateTime.now();
    }
}

enum Role {
    USER, ADMIN
}
