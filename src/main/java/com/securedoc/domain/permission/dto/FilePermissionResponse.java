package com.securedoc.domain.permission.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class FilePermissionResponse {

    private boolean success;
    private String message;
    private Data data;

    @Getter
    @Builder
    public static class Data {
        private Long shareId;
        private File file;
        private SharedUserInfo sharedUser;
        private boolean isPinProtected;
        private LocalDateTime shareDate;
        private LocalDateTime expireDate;
    }

    @Getter
    @Builder
    public static class File {
        private Long fileId;
        private String originalFilename;
    }

    @Getter
    @Builder
    public static class SharedUserInfo {
        private String userId;
        private String username;
    }

}
