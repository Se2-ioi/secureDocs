package com.securedoc.domain.file;

import com.securedoc.domain.exception.ForbiddenException;
import com.securedoc.domain.exception.NotFoundException;
import com.securedoc.domain.file.dto.*;
import com.securedoc.domain.permission.FilePermission;
import com.securedoc.domain.permission.FilePermissionRepository;
import com.securedoc.domain.user.User;
import com.securedoc.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileServiceImp1 implements FileService{

    private final UserRepository userRepository;
    private final FileRepository fileRepository;
    private final FilePermissionRepository filePermissionRepository;

    private final String UPLOADPATH = "C:/uploads/";

    @Override
    // 파일 업로드 기능
    public FileUploadResponse upload(FileUploadRequest uploadRequest, Long userId) {

        MultipartFile file = uploadRequest.getFile();

        // 1. 파일 검증
        validateFile(file);

        // 2. 파일 저장
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(file.getOriginalFilename());
        /* 파일명을 통한 내용 노출 방지 */
        String newFilename = UUID.randomUUID() + "." + extension;

        String upLoadPath = UPLOADPATH + "user_" + userId + "/";
        Path directory = Paths.get(upLoadPath);
        try {
            if(!Files.exists(directory)) {
                Files.createDirectories(directory);
            }
            Path filePath = directory.resolve(newFilename);
            file.transferTo(filePath.toFile());
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }

        // 3. DB 저장
        File fileEntity = File.builder()
                .userId(userId)
                .filename(newFilename)
                .originalFilename(originalFilename)
                .fileSize(file.getSize())
                .fileType(file.getContentType())
                .fileExtension(extension)
                .build();

        fileRepository.save(fileEntity);

        // 4. 응답
        return FileUploadResponse.builder()
                .success(true)
                .message("파일 업로드 완료")
                .data (
                        FileUploadResponse.Data.builder()
                                .fileId(fileEntity.getFileId())
                                .filename(fileEntity.getFilename())
                                .originalFilename(fileEntity.getOriginalFilename())
                                .fileSize(fileEntity.getFileSize())
                                .fileType(fileEntity.getFileType())
                                .fileExtension(fileEntity.getFileExtension())
                                .userId(fileEntity.getUserId())
                                .createDate(fileEntity.getCreateDate())
                                .build()
                )
                .build();
    }


    private void validateFile(MultipartFile file) {
        // 파일 존재 여부 확인
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 포함되지 않았습니다.");
        }

        // 파일 크기 초과
        if (file.getSize() > 50 * 1024 * 1024) {
            throw new IllegalArgumentException("파일 크기가 허용 범위를 초과했습니다. (최대 50MB)");
        }

        // 허용되지 않는 파일 타입
        String extension = getFileExtension(file.getOriginalFilename());
        if (!isAllowExtension(extension)) {
            throw new IllegalArgumentException("허용되지 않는 파일 확장자입니다.");
        }

        // TO-do 매직넘버 구현
    }

    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    private boolean isAllowExtension(String extension) {
        return Set.of("pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt", "jpg", "png", "zip")
                .contains(extension);
    }


    @Override
    // 파일 다운로드 기능
    public ResponseEntity<?> download(Long fileId, FileDownloadRequest request, Long userId) {

        File fileEntity = fileRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다."));

        // 다운로드 권한 - 소유자 확인
        if (!fileEntity.getUserId().equals(userId)) {

            // 다운로드 권한 - 공유 대상자 확인
            Optional<FilePermission> permission = filePermissionRepository
                    .findByFileIdAndUserId(fileId, userId);

            if (permission.isEmpty()) {
                throw new IllegalArgumentException("다운로드 권한이 없습니다.");
            }

            // 공유 받은 사람 -> 공유 기한 체크
            FilePermission perm = permission.get();
            if (perm.getExpireDate().isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("다운로드 공유 기한이 만료되었습니다.");
            }

            // 공유 받은 사람 -> PIN 번호 체크
            Integer inputPin = request.getPin();
            if (perm.getPin() != null) {
                if (inputPin == null) {
                    throw new IllegalArgumentException("PIN 번호를 입력해주세요.");
                }
                if (!BCrypt.checkpw(inputPin.toString(), perm.getPin())) {
                    throw new IllegalArgumentException("PIN 번호가 일치하지 않습니다.");
                }
            }
        }

        // 드디어 파일 반환
        String DownloadPath = UPLOADPATH + "user_" + fileEntity.getUserId() + "/" + fileEntity.getFilename();
        Path path = Paths.get(DownloadPath);

        // TO-Do 삭제된 파일도 추가하기
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("파일을 찾을 수 없습니다.");
        }

        if (fileEntity.getDeleteDate() != null) {
            throw new NotFoundException("FILE_NOT_FOUND", "파일을 찾을 수 없습니다.");
        }

        Resource resource = new FileSystemResource(path.toFile());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileEntity.getFileType()))
                .contentLength(fileEntity.getFileSize())
                .header(
                        "Content-Disposition",
                        "attachment; filename=\"" + fileEntity.getOriginalFilename() + "\""
                )
                .body(resource);
    }

    @Override
    // 파일 삭제 기능
    public void delete(Long fileId, Long userId) {
        // 파일 조회
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new NotFoundException("FILE_NOT_FOUND", "파일을 찾을 수 없습니다."));

        // 소유자 여부 확인
        if (!file.getUserId().equals(userId)) {
            throw new ForbiddenException("PERMISSION_DENIED", "삭제 권한이 없습니다.");
        }

        // deleteDate 설정
        file.setDeleteDate();
        fileRepository.save(file);
    }

    @Override
    // 내 파일 목록
    public FileMyListResponse myList(Long userId, int page, int limit, String sort, String order) {

        // 파라미터 검증
        if(page < 1) {
            throw new IllegalArgumentException("잘못된 페이지 번호입니다.");
        }
        if (limit > 10) {
            limit = 10;
        }

        // 페이지 정렬
        Sort.Direction direction = order.equalsIgnoreCase("asc")
            ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort fileSort = Sort.by(direction, sort);
        Pageable pageable = PageRequest.of(page-1, limit, fileSort);

        // 데이터, 페이징 인터페이스 사용
        Page<File> filePage = fileRepository.findByUserIdAndDeleteDateIsNull(userId, pageable);

        // File entity -> DTO response 값 변환
        List<FileMyListResponse.myList> myLists = filePage.getContent().stream()
                .map(file -> FileMyListResponse.myList.builder()
                        .fileId(file.getFileId())
                        .originalFilename(file.getOriginalFilename())
                        .fileSize(file.getFileSize())
                        .fileExtension(file.getFileExtension())
                        .createDate(file.getCreateDate())
                        .build())
                .toList();

        FileMyListResponse.Pagination pagination = FileMyListResponse.Pagination.builder()
                .currentPage(page)
                .totalPage(filePage.getTotalPages())
                .totalItems(filePage.getTotalElements())
                .itemsPerPage(limit)
                .build();

        return FileMyListResponse.builder()
                .success(true)
                .data(FileMyListResponse.Data.builder()
                        .files(myLists)
                        .pagination(pagination)
                        .build())
                .build();
    }

    @Override
    // 공유 받은 파일 목록
    public FileShareListResponse shareList(Long userId, int page, int limit, String sort, String order) {

        // 파라미터 검증
        if (page < 1) {
            throw new IllegalArgumentException("잘못된 페이지 번호입니다.");
        }
        if (limit > 10) {
            limit = 10;
        }

        // 페이지 정렬
        Sort.Direction direction = order.equalsIgnoreCase("asc")
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort fileSort = Sort.by(direction, sort);
        Pageable pageable = PageRequest.of(page - 1, limit, fileSort);

        Page<FilePermission> sharePage = filePermissionRepository.findByUserId(userId, pageable);

        List<FileShareListResponse.ShareList> shareLists = sharePage.getContent().stream()
                .map(share -> {

                    // 파일 없는 경우 (삭제 포함)
                    File file = fileRepository.findById(share.getFileId())
                            .orElse(null);
                    if (file == null || file.getDeleteDate() != null) {
                        return null;
                    }

                    // owner 정보 없는 경우
                    User owner = userRepository.findById(file.getUserId())
                            .orElse(null);
                    if (owner == null) {
                        return null;
                    }

                    return FileShareListResponse.ShareList.builder()
                            .fileId(file.getFileId())
                            .originalFilename(file.getOriginalFilename())
                            .filesize(file.getFileSize())
                            .fileExtension(file.getFileExtension())
                            .owner(FileShareListResponse.Owner.builder()
                                    .userId(owner.getUserId())
                                    .username(owner.getUsername())
                                    .build())
                            .shareDate(share.getShareDate())
                            .build();
                })
                .filter(shareList -> shareList != null)
                .toList();

        FileShareListResponse.Pagination pagination = FileShareListResponse.Pagination.builder()
                .currentPage(page)
                .totalPage(sharePage.getTotalPages())
                .totalItems(sharePage.getTotalElements())
                .itemsPerPage(limit)
                .build();

        return FileShareListResponse.builder()
                .success(true)
                .data(FileShareListResponse.Data.builder()
                        .files(shareLists)
                        .pagination(pagination)
                        .build())
                .build();
    }
}
