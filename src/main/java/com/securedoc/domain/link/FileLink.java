package com.securedoc.domain.link;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "file_link")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long linkId;

    @Column(nullable = false)
    private Long fileId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String shareUuid;

    @Column(name = "pin_hash")
    private String pin;

    @Builder.Default
    private LocalDateTime createDate = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime expireDate;

    @Column(nullable = false)
    @Builder.Default
    private boolean isActive = true;
}
