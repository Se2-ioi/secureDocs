package com.securedoc.domain.link;

import com.securedoc.domain.exception.BadRequestExcetpion;
import com.securedoc.domain.exception.ForbiddenException;
import com.securedoc.domain.exception.NotFoundException;
import com.securedoc.domain.file.File;
import com.securedoc.domain.file.FileRepository;
import com.securedoc.domain.link.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileLinkServiceImp1 implements FileLinkService {

    private final FileLinkRepository fileLinkRepository;
    private final FileRepository fileRepository;

    private final String UPLOADPATH = "C:/uploads/";

    // 링크 공유
    @Override
    public FileLinkResponse linkShare(FileLinkRequest request, Long userId) {

        // 파일 및 소유자 확인
        File linkFile = fileRepository.findByFileIdAndDeleteDateIsNull(request.getFileId())
                .orElseThrow(() -> new NotFoundException("FILE_NOT_FOUND", "파일을 찾을 수 없습니다."));
        if(!userId.equals(linkFile.getUserId())) {
            throw new ForbiddenException("PERMISSION_DENIED", "공유 권한이 없습니다.");
        }

        // PIN Hash 처리
        String pinHash = null;
        if(request.getPin() != null) {
            pinHash = BCrypt.hashpw(request.getPin(), BCrypt.gensalt());
        }

        // 공유 기한 설정
        LocalDateTime expireDate = calExpireDate(request.getExpires());

        // Uuid -> Url 생성
        String shareUuid = UUID.randomUUID().toString();
        String url = "https://securedoc.com/s/" + shareUuid;

        FileLink link = FileLink.builder()
                .fileId(request.getFileId())
                .userId(userId)
                .shareUuid(shareUuid)
                .pin(pinHash)
                .expireDate(expireDate)
                .build();

        FileLink saved = fileLinkRepository.save(link);

        return createResponse(saved, url);
    }

    private FileLinkResponse createResponse(FileLink link, String url) {

        // Data 구현
        FileLinkResponse.Data data = FileLinkResponse.Data.builder()
                .shareUuid(link.getShareUuid())
                .url(url)
                .isPinProtected(link.getPin() != null)
                .expireDate(link.getExpireDate())
                .build();

        // Response
        return FileLinkResponse.builder()
                .success(true)
                .message("링크가 생성되었습니다.")
                .data(data)
                .build();
    }

    // 공유 기한 로직
    private LocalDateTime calExpireDate(String expires) {
        LocalDateTime now = LocalDateTime.now();
        if(expires.equals("1h")) {
            return now.plusHours(1);
        } else if(expires.equals("24h")) {
            return now.plusHours(24);
        } else if(expires.equals("7d")) {
            return now.plusDays(7);
        } else {
            return now.plusHours(24);
        }
    }

    // 링크 공유 해제
    @Override
    public void linkUnShare(String shareUuid, Long userId) {

        // 링크 공유 정보 조회
        FileLink link = fileLinkRepository.findByShareUuid(shareUuid)
                .orElseThrow(() -> new NotFoundException("LINK_NOT_FOUND", "공유 링크를 찾을 수 없습니다."));

        // 소유자 확인 (공유 취소 권한)
        if(!userId.equals(link.getUserId())) {
            throw new ForbiddenException("PERMISSION_DENIED", "링크 취소 권한이 없습니다.");
        }

        fileLinkRepository.delete(link);
    }

    // 링크 공유 설정 변경
    @Override
    public FileLinkChangeResponse changeLinkShare(FileLinkChangeRequest request, String shareUuid, Long userId) {

        // 링크 공유 정보 조회
        FileLink link = fileLinkRepository.findByShareUuid(shareUuid)
                .orElseThrow(() -> new NotFoundException("LINK_NOT_FOUND", "공유 링크를 찾을 수 없습니다."));

        // 소유자 확인 (공유 변경 권한)
        if(!userId.equals(link.getUserId())) {
            throw new ForbiddenException("PERMISSION_DENIED", "공유 권한이 없습니다.");
        }

        String url = "https://securedoc.com/s/" + shareUuid;
        
        // PIN 번호 변경
        boolean pinChanged = (request.getPin() != null);
        String newPinHash = null;
        if(pinChanged) {
            newPinHash = BCrypt.hashpw(request.getPin(), BCrypt.gensalt());
        }
        link.setPin(newPinHash);

        // 공유 기한 변경
        LocalDateTime newExpireDate = calExpireDate(request.getExpires());
        link.setExpireDate(newExpireDate);

        // save
        FileLink saved = fileLinkRepository.save(link);

        return createChangeResponse(saved, url, pinChanged);
    }
    
    public FileLinkChangeResponse createChangeResponse(FileLink link, String url, boolean pinChanged) {
        // data
        FileLinkChangeResponse.Data data = FileLinkChangeResponse.Data.builder()
                .shareUuid(link.getShareUuid())
                .url(url)
                .isPinProtected(link.getPin() != null)
                .pinChanged(pinChanged)
                .expireDate(link.getExpireDate())
                .build();

        return FileLinkChangeResponse.builder()
                .success(true)
                .message("링크 공유 설정이 변경되었습니다.")
                .data(data)
                .build();
    }

    // 링크 다운로드
    @Override
    public ResponseEntity<?> linkDownload(String shareUuid, FileLinkDownloadRequest request) {

        // 링크 공유 정보 조회
        FileLink link = fileLinkRepository.findByShareUuid(shareUuid)
                .orElseThrow(() -> new NotFoundException("LINK_NOT_FOUND", "해당 링크를 찾을 수 없습니다."));

        // isActive 확인
        if(!link.isActive()) {
            throw new ForbiddenException("LINK_INACTIVE", "링크가 비활성화 상태입니다.");
        }

        // 공유 기한 확인
        if(link.getExpireDate().isBefore(LocalDateTime.now())) {
            throw new ForbiddenException("LINK_EXPIRED", "링크가 만료되었습니다.");
        }

        // PIN 번호 일치 여부
        String inputPin = request.getPin();
        if(link.getPin() != null) {
            if(inputPin == null) {
                throw new BadRequestExcetpion("PIN_REQUIRED", "PIN 번호를 입력해주세요.");
            }
            else if(!BCrypt.checkpw(inputPin, link.getPin())) {
                throw new BadRequestExcetpion("INVALID_PIN", "PIN 번호가 일치하지 않습니다.");
            }
        }

        // 파일 조회
        File file = fileRepository.findByFileIdAndDeleteDateIsNull(link.getFileId())
                .orElseThrow(() -> new NotFoundException("FILE_NOT_FOUND", "파일을 찾을 수 없습니다."));

        String DownloadPath = UPLOADPATH + "user_" + file.getUserId() + "/" + file.getFilename();
        Path path = Paths.get(DownloadPath);

        if(!Files.exists(path)) {
            throw new NotFoundException("FILE_NOT_FOUND", "파일을 찾을 수 없습니다.");
        }

        Resource resource = new FileSystemResource(path.toFile());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getFileType()))
                .contentLength(file.getFileSize())
                .header("Content-Disposition", "attachment; filename=\"" + file.getOriginalFilename() + "\"")
                .body(resource);
    }
}
