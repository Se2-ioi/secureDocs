package com.securedoc.domain.file;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "files")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long fileId;

    @Column(nullable = false)
    // 파일 소유자의 id(User -> id entity)
    private Long userId;

    @Column(nullable = false, length = 255)
    private String filename;

    @Column(nullable = false, length = 255)
    private String originalFilename;

    @Column(nullable = false)
    private Long fileSize;

    @Column(nullable = false, length = 100)
    private String fileType;

    @Column(nullable = false, length = 20)
    private String fileExtension;

    /* 생성일자, 삭제일자 자동 생성 */
    @Builder.Default
    private LocalDateTime createDate = LocalDateTime.now();
    private LocalDateTime deleteDate;

    @PreRemove
    public void setDeleteDate() {
        this.deleteDate = LocalDateTime.now();
    }
}
