package com.securedoc.domain.permission;

import com.securedoc.domain.permission.dto.FilePermissionChangeRequest;
import com.securedoc.domain.permission.dto.FilePermissionChangeResponse;
import com.securedoc.domain.permission.dto.FilePermissionRequest;
import com.securedoc.domain.permission.dto.FilePermissionResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FilePermissionController {

    private final FilePermissionService filePermissionService;

    @PostMapping("/share")
    public ResponseEntity<FilePermissionResponse> share(
            @Valid @RequestBody FilePermissionRequest request,
            HttpServletRequest httpRequest) {

        Long userId = Long.parseLong((String) httpRequest.getAttribute("userId"));
        FilePermissionResponse response = filePermissionService.share(request, userId);
        return ResponseEntity.status(201).body(response);
    }

    @DeleteMapping("/share/{shareId}")
    public ResponseEntity<Map<String, Object>> unshare(
            @PathVariable Long shareId,
            HttpServletRequest httpRequest) {
        Long userId = Long.parseLong((String) httpRequest.getAttribute("userId"));
        filePermissionService.unshare(shareId, userId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "공유가 취소되었습니다.");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/share/{shareId}")
    public ResponseEntity<FilePermissionChangeResponse> shareChange(
            @PathVariable Long shareId,
            @Valid @RequestBody FilePermissionChangeRequest request,
            HttpServletRequest httpRequest) {

        Long userId = Long.parseLong((String) httpRequest.getAttribute("userId"));
        FilePermissionChangeResponse response = filePermissionService.shareChange(request, shareId, userId);
        return ResponseEntity.ok(response);

    }

}
