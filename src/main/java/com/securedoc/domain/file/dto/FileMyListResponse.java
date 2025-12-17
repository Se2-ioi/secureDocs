package com.securedoc.domain.file.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class FileMyListResponse {

    private boolean success;
    private Data data;

    @Getter
    @Builder
    public static class Data {
        private List<myList> files; // 파일이 여러개 -> List
        private Pagination pagination;
    }

    @Getter
    @Builder
    public static class myList {
        private Long fileId;
        private String originalFilename;
        private Long fileSize;
        private String fileExtension;
        private LocalDateTime createDate;
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
