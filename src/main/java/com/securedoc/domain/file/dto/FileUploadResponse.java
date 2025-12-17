package com.securedoc.domain.file.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class FileUploadResponse {

    private boolean success;
    private String message;
    private Data data;

    @Getter
    @Builder
    public static class Data {
        private Long fileId;
        private String filename;
        private String originalFilename;
        private Long fileSize;
        private String fileType;
        private String fileExtension;
        private Long userId;
        private LocalDateTime createDate;
    }

}
