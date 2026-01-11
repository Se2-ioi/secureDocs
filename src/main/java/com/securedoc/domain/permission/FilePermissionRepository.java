package com.securedoc.domain.permission;

import com.securedoc.domain.file.File;
import com.securedoc.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FilePermissionRepository extends JpaRepository<FilePermission, Long> {
    Optional<FilePermission> findByFileIdAndUserId(Long fileId, String userId);
    Page<FilePermission> findByUserId(String userId, Pageable pageable);

    List<FilePermission> findByFileId(Long fileId);
    void deleteByFileId(Long fileId);
}
