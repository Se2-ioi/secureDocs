package com.securedoc.domain.permission;

import com.securedoc.domain.exception.BadRequestExcetpion;
import com.securedoc.domain.exception.ForbiddenException;
import com.securedoc.domain.exception.NotFoundException;
import com.securedoc.domain.file.File;
import com.securedoc.domain.file.FileRepository;
import com.securedoc.domain.permission.dto.FilePermissionChangeRequest;
import com.securedoc.domain.permission.dto.FilePermissionChangeResponse;
import com.securedoc.domain.permission.dto.FilePermissionRequest;
import com.securedoc.domain.permission.dto.FilePermissionResponse;
import com.securedoc.domain.user.User;
import com.securedoc.domain.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FilePermissionServiceImp1 implements FilePermissionService{

    private final FilePermissionRepository filePermissionRepository;
    private final UserRepository userRepository;
    private final FileRepository fileRepository;

    @Override
    @Transactional
    // 공유 설정
    public FilePermissionResponse share(FilePermissionRequest request, Long userId) {

        // 공유 받을 사용자 확인
        User sharedUser = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND", "해당 아이디의 사용자를 찾을 수 없습니다."));

        // 본인에게 공유 -> 체크
        if (sharedUser.getId().equals(userId)) {
            throw new BadRequestExcetpion("CANNOT_SHARE_TO_SELF", "본인에게는 공유할 수 없습니다. 다른 사용자를 선택해주세요.");
        }

        // 파일 존재 및 소유자 확인
        File sharedFile = fileRepository.findByFileIdAndDeleteDateIsNull(request.getFileId())
                .orElseThrow(() -> new NotFoundException("FILE_NOT_FOUND", "파일을 찾을 수 없습니다."));
        if(!sharedFile.getUserId().equals(userId)) {
            throw new ForbiddenException("PERMISSION_DENIED", "공유 권한이 없습니다.");
        }

        // PIN Hash 처리
        String pinHash = null;
        if (request.getPin() != null) {
            pinHash = BCrypt.hashpw(request.getPin(), BCrypt.gensalt());
        }

        // 공유 기한 설정
        LocalDateTime expireDate = calExpireDate(request.getExpires());

        FilePermission permission = FilePermission.builder()
                .fileId(sharedFile.getFileId())
                .userId(sharedUser.getUserId())
                .pin(pinHash)
                .shareDate(LocalDateTime.now())
                .expireDate(expireDate)
                .build();

        FilePermission saved = filePermissionRepository.save(permission);

        return createResponse(saved, sharedFile, sharedUser);
    }

    private FilePermissionResponse createResponse(FilePermission permission, File sharedFile, User sharedUser) {
        // File 구현
        FilePermissionResponse.File fileInfo = FilePermissionResponse.File.builder()
                .fileId(sharedFile.getFileId())
                .originalFilename(sharedFile.getOriginalFilename())
                .build();

        // SharedUser 구현
        FilePermissionResponse.SharedUserInfo sharedUserInfo = FilePermissionResponse.SharedUserInfo.builder()
                .userId(sharedUser.getUserId())
                .username(sharedUser.getUsername())
                .build();

        // Data 구현
        FilePermissionResponse.Data data = FilePermissionResponse.Data.builder()
                .shareId(permission.getShareId())
                .file(fileInfo)
                .sharedUser(sharedUserInfo)
                .isPinProtected(permission.getPin() != null)
                .shareDate(permission.getShareDate())
                .expireDate(permission.getExpireDate())
                .build();

        // Response
        return FilePermissionResponse.builder()
                .success(true)
                .message("파일이 공유되었습니다.")
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

    // 공유 해제
    @Override
    @Transactional
    public void unshare(Long shareId, Long userId) {

        // 공유 정보 조회
        FilePermission filePermission = filePermissionRepository.findById(shareId)
                .orElseThrow(() -> new NotFoundException("SHARE_NOT_FOUND", "공유 정보를 찾을 수 없습니다."));

        // 파일 조회
        File file = fileRepository.findByFileIdAndDeleteDateIsNull(filePermission.getFileId())
                .orElseThrow(() -> new NotFoundException("FILE_NOT_FOUND", "파일을 찾을 수 없습니다."));

        // 소유자 확인 (공유 취소 권한)
        if(!userId.equals(file.getUserId())) {
            throw new ForbiddenException("PERMISSION_DENIED", "공유 취소 권한이 없습니다.");
        }

        filePermissionRepository.delete(filePermission);
    }

    // 공유 설정 변경
    @Override
    @Transactional
    public FilePermissionChangeResponse shareChange(FilePermissionChangeRequest request, Long shareId, Long userId) {

        // 공유 정보 조회
        FilePermission filePermission = filePermissionRepository.findById(shareId)
                .orElseThrow(() -> new NotFoundException("SHARE_NOT_FOUND", "공유 정보를 찾을 수 없습니다."));

        // 파일 조회
        File file = fileRepository.findByFileIdAndDeleteDateIsNull(filePermission.getFileId())
                .orElseThrow(() -> new NotFoundException("FILE_NOT_FOUND", "파일을 찾을 수 없습니다."));

        // 소유자 확인 (설정 변경 권한)
        if(!userId.equals(file.getUserId())) {
            throw new ForbiddenException("PERMISSION_DENIED", "공유 권한이 없습니다.");
        }

        // PIN 번호 변경
        boolean pinChanged = (request.getPin() != null);
        String newPinHash = null;
        if (pinChanged) {
            newPinHash = BCrypt.hashpw(request.getPin(), BCrypt.gensalt());
        }
        filePermission.setPin(newPinHash);

        // 공유 기한 변경
        LocalDateTime newExpireDate = calExpireDate(request.getExpires());
        filePermission.setExpireDate(newExpireDate);

        // save
        FilePermission saved = filePermissionRepository.save(filePermission);

        return createChangeResponse(saved, file.getFileId(), pinChanged);
    }

    private FilePermissionChangeResponse createChangeResponse(FilePermission permission, Long fileId, boolean pinChanged) {

        FilePermissionChangeResponse.Data data = FilePermissionChangeResponse.Data.builder()
                .shareId(permission.getShareId())
                .fileId(fileId)
                .isPinProtected(permission.getPin() != null)
                .pinChanged(pinChanged)
                .expireDate(permission.getExpireDate())
                .build();

        return FilePermissionChangeResponse.builder()
                .success(true)
                .message("공유 설정이 변경되었습니다.")
                .data(data)
                .build();
    }
}
