package com.securedoc.domain.permission;

import com.securedoc.domain.permission.dto.FilePermissionChangeRequest;
import com.securedoc.domain.permission.dto.FilePermissionChangeResponse;
import com.securedoc.domain.permission.dto.FilePermissionRequest;
import com.securedoc.domain.permission.dto.FilePermissionResponse;

public interface FilePermissionService {

    FilePermissionResponse share(FilePermissionRequest request, Long userId);
    void unshare(Long shareId, Long userId);
    FilePermissionChangeResponse shareChange(FilePermissionChangeRequest request, Long shareId, Long userId);
}
