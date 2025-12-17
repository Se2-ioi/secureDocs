package com.securedoc.domain.permission;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "file_permission")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long fileId;

    @Column(nullable = false)
    private Long userId;

    @Column
    private String pin;

    @Builder.Default
    private LocalDateTime shareDate = LocalDateTime.now();

    @Column
    private LocalDateTime expireDate;

}
