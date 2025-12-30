package com.securedoc.domain.link.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class FileLinkChangeResponse {

    private boolean success;
    private String message;
    private Data data;

    @Getter
    @Builder
    public static class Data {
        private String shareUuid;
        private String url;
        private boolean isPinProtected;
        private boolean pinChanged;
        private LocalDateTime expireDate;
    }
}
