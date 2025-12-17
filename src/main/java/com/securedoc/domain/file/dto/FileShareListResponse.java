package com.securedoc.domain.file.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class FileShareListResponse {

    private boolean success;
    private Data data;

    @Getter
    @Builder
    public static class Data {
        private List<ShareList> files;
        private Pagination pagination;
    }

    @Getter
    @Builder
    public static class ShareList {
        private Long fileId;
        private String originalFilename;
        private Long filesize;
        private String fileExtension;
        private Owner owner;
        private LocalDateTime shareDate;
    }

    @Getter
    @Builder
    public static class Owner {
        private String userId;
        private String username;
    }


    @Getter
    @Builder
    public static class Pagination {
        private int currentPage; // 현재 페이지
        private int totalPage; // 전체 페이지
        private Long totalItems; // 전체 파일
        private int itemsPerPage; // 페이지 당 파일
    }
}
