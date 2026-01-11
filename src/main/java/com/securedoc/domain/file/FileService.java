package com.securedoc.domain.file;


import com.securedoc.domain.file.dto.*;
import org.springframework.http.ResponseEntity;

public interface FileService {

    FileUploadResponse upload(FileUploadRequest uploadRequest, Long userId);
    ResponseEntity<?> download(Long fileId, FileDownloadRequest downloadRequest, Long userId);
    public void delete(Long fileId, Long userId);

    FileMyListResponse myList(Long userId, int page, int limit, String sort, String order);
    FileShareListResponse shareList(Long userId, int page, int limit, String sort, String order);
    FileDetailResponse fileDetail(Long fileId, Long userId);

    TrashFileResponse trashList(Long userId, int page, int limit);
    public void permanentDelete(Long fileId, Long userId);
    TrashRestoreResponse restoreFile(Long fileId, Long userId);

    public void cleanUpTrashFile();
}
