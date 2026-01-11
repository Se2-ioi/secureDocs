package com.securedoc.domain.file.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TrashRestoreResponse {

    private boolean success;
    private String message;
    private Data data;

    @Getter
    @Builder
    public static class Data {
        private Long fileId;
        private String originalFilename;
        private Long fileSize;
        private String fileExtension;
        // 복구해도 createDate는 그대로
        private LocalDateTime createDate;
    }
}
