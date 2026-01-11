package com.securedoc.domain.file;

import com.securedoc.domain.file.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping(value = "/upload")
    public ResponseEntity<?> upload(

            @RequestParam("file") MultipartFile file,
            @RequestParam("description") String description,
            @RequestParam("metadata") String metadata,
            HttpServletRequest request) {
        System.out.println("=== Upload 요청 받음 ===");
        System.out.println("File: " + file);
        System.out.println("Description: " + description);
        try {
            System.out.println("userId 추출 시작");
            Long userId = Long.parseLong((String) request.getAttribute("userId"));
            System.out.println("userId: " + userId);

            FileUploadRequest uploadRequest = new FileUploadRequest();
            uploadRequest.setFile(file);
            uploadRequest.setDescription(description);
            uploadRequest.setMetadata(metadata);

            FileUploadResponse uploadResponse = fileService.upload(uploadRequest, userId);
            return ResponseEntity.status(201).body(uploadResponse);

        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error_code", "VALIDATION_ERROR");
            error.put("message", e.getMessage());
            return ResponseEntity.status(400).body(error);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error_code", "INTERNAL_SERVER_ERROR");
            error.put("message", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    @PostMapping("/{fileId}/download")
    public ResponseEntity<?> download(
            @PathVariable Long fileId,
            @RequestBody FileDownloadRequest downloadRequest,
            HttpServletRequest httpRequest) {
        try {
            Long userId = Long.parseLong((String) httpRequest.getAttribute("userId"));
            return fileService.download(fileId, downloadRequest, userId);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error_code", "VALIDATION_ERROR");
            error.put("message", e.getMessage());
            return ResponseEntity.status(400).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error_code", "INTERNAL_SERVER_ERROR");
            error.put("message", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<?> delete(
            @PathVariable Long fileId,
            HttpServletRequest httpRequest) {
        try {
            Long userId = Long.parseLong((String) httpRequest.getAttribute("userId"));

            fileService.delete(fileId, userId);

            // 응답 (단순, DTO 생성 안함)
            Map<String, Object> response = new HashMap<>(); // JSON 객체 생성
            response.put("success", true);
            response.put("message", "파일이 휴지통으로 이동되었습니다.");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error_code", "VALIDATION_ERROR");
            error.put("message", e.getMessage());
            return ResponseEntity.status(400).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error_code", "INTERNAL_SERVER_ERROR");
            return ResponseEntity.status(500).body(error);
        }
    }

    @GetMapping("/my")
    public ResponseEntity<?> myList (
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int  limit,
            @RequestParam(defaultValue = "createDate") String sort,
            @RequestParam(defaultValue = "desc") String order,
            HttpServletRequest httpRequest
    ) {
        try {
            Long userId = Long.parseLong((String) httpRequest.getAttribute("userId"));
            FileMyListResponse myListResponse = fileService.myList(userId, page, limit, sort, order);
            return ResponseEntity.status(200).body(myListResponse);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error_code", "VALIDATION_ERROR");
            error.put("message", e.getMessage());
            return ResponseEntity.status(400).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error_code", "INTERNAL_SERVER_ERROR");
            return ResponseEntity.status(500).body(error);
        }
    }

    @GetMapping("/share")
    public ResponseEntity<?> shareList (
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int  limit,
            @RequestParam(defaultValue = "shareDate") String sort,
            @RequestParam(defaultValue = "desc") String order,
            HttpServletRequest httpRequest
    ) {
        try {
            Long userId = Long.parseLong((String) httpRequest.getAttribute("userId"));
            FileShareListResponse shareListResponse = fileService.shareList(userId, page, limit, sort, order);
            return ResponseEntity.status(200).body(shareListResponse);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error_code", "VALIDATION_ERROR");
            error.put("message", e.getMessage());
            return ResponseEntity.status(400).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error_code", "INTERNAL_SERVER_ERROR");
            return ResponseEntity.status(500).body(error);
        }
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<FileDetailResponse> getFileDetail(
            @PathVariable Long fileId,
            HttpServletRequest httpRequest
    ) {
        Long userId = Long.parseLong((String) httpRequest.getAttribute("userId"));
        FileDetailResponse response = fileService.fileDetail(fileId, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/trash")
    public ResponseEntity<TrashFileResponse> getTrashList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int  limit,
            HttpServletRequest httpRequest
    ) {
        Long userId = Long.parseLong((String) httpRequest.getAttribute("userId"));
        TrashFileResponse response = fileService.trashList(userId, page, limit);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{fileId}/restore")
    public ResponseEntity<?> restore(
            @PathVariable Long fileId,
            HttpServletRequest httpRequest
    ) {
        Long userId = Long.parseLong((String) httpRequest.getAttribute("userId"));
        TrashRestoreResponse response = fileService.restoreFile(fileId, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{fileId}/permanent")
    public ResponseEntity<?> permanentDelete(
            @PathVariable Long fileId,
            HttpServletRequest httpRequest
    ) {
        Long userId = Long.parseLong((String) httpRequest.getAttribute("userId"));
        fileService.permanentDelete(fileId, userId);

        Map<String, Object> response = new HashMap<>(); // JSON 객체 생성
        response.put("success", true);
        response.put("message", "파일이 영구 삭제되었습니다.");

        return ResponseEntity.ok(response);
    }
}
