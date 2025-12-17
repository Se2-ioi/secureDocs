package com.securedoc.domain.permission;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FilePermissionRepository extends JpaRepository<FilePermission, Long> {
    Optional<FilePermission> findByFileIdAndUserId(Long fileId, Long userId);
    Page<FilePermission> findByUserId(Long userId, Pageable pageable);
}
