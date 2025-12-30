package com.securedoc.domain.link;

import com.securedoc.domain.link.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/share")
@RequiredArgsConstructor
public class FileLinkController {

    private final FileLinkService fileLinkService;

    @PostMapping("/link")
    public ResponseEntity<FileLinkResponse> linkShare(
            @Valid @RequestBody FileLinkRequest request,
            HttpServletRequest httpRequest) {

        Long userId = Long.parseLong((String) httpRequest.getAttribute("userId"));
        FileLinkResponse response = fileLinkService.linkShare(request, userId);
        return ResponseEntity.status(201).body(response);
    }

    @DeleteMapping("/link/{shareUuid}")
    public ResponseEntity<Map<String, Object>> linkUnShare(
            @PathVariable String shareUuid,
            HttpServletRequest httpRequest) {
        Long userId = Long.parseLong((String) httpRequest.getAttribute("userId"));
        fileLinkService.linkUnShare(shareUuid, userId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "링크 공유가 취소되었습니다.");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/link/{shareUuid}")
    public ResponseEntity<FileLinkChangeResponse> changeLinkShare(
            @PathVariable String shareUuid,
            @Valid @RequestBody FileLinkChangeRequest request,
            HttpServletRequest httpRequest) {

        Long userId = Long.parseLong((String) httpRequest.getAttribute("userId"));
        FileLinkChangeResponse response = fileLinkService.changeLinkShare(request, shareUuid, userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/link/{shareUuid}")
    public ResponseEntity<?> linkDownload(
            @PathVariable String shareUuid,
            @Valid @RequestBody FileLinkDownloadRequest request) {
        return fileLinkService.linkDownload(shareUuid, request);
    }
}
