package com.securedoc.domain.permission;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "file_permission")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long shareId;

    @Column(nullable = false)
    private Long fileId;

    // 공유 대상자
    @Column(nullable = false)
    private Long userId;

    @Column(name = "pin_hash")
    private String pin;

    @Builder.Default
    private LocalDateTime shareDate = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime expireDate;

}
