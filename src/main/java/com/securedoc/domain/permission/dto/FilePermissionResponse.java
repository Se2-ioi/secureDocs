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
        private UserInfo user; // 유저 번호
        private File file;
        private ShareInfo share;
        private boolean isPinProtected;
        private LocalDateTime shareDate;
        private LocalDateTime expireDate;
    }

    @Getter
    @Builder
    public static class UserInfo {
        private String userId;
        private String username;
    }

    @Getter
    @Builder
    public static class File {
        private Long fileId;
        private String originalFilename;
    }

    @Getter
    @Builder
    public static class ShareInfo {
        private String userId;
        private String username;
    }

}
