package com.securedoc.domain.file.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class FileDetailResponse {

    private boolean success;
    private Data data;

    @Getter
    @Setter
    @Builder
    public static class Data {
        private Long fileId;
        private String filename;
        private String originalFilename;
        private Long fileSize;
        private String fileType;
        private String fileExtension;
        private Owner owner;
        private List<Share> shareList;
        private LocalDateTime createDate;
    }

    @Getter
    @Setter
    @Builder
    public static class Owner {
        private Long id;
        private String username;
    }

    @Getter
    @Setter
    @Builder
    public static class Share {
        private Long shareId;
        private String Id;
        private String username;
        private LocalDateTime shareDate;
        private LocalDateTime expireDate;
        private boolean isPinProtected;
    }

}
