package com.securedoc.domain.permission.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class FilePermissionChangeResponse {

    private boolean success;
    private String message;
    private Data data;

    @Getter
    @Builder
    public static class Data {
        private Long shareId;
        private Long fileId;
        private boolean isPinProtected;
        private boolean pinChanged;
        private LocalDateTime expireDate;
    }

}
