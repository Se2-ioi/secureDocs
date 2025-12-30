package com.securedoc.domain.link;

import com.securedoc.domain.link.dto.*;
import org.springframework.http.ResponseEntity;

public interface FileLinkService {

    FileLinkResponse linkShare(FileLinkRequest request, Long userId);
    void linkUnShare(String shareUuid, Long userId);
    FileLinkChangeResponse changeLinkShare(FileLinkChangeRequest request, String shareUuid, Long userId);
    ResponseEntity<?> linkDownload(String shareUuid, FileLinkDownloadRequest request);
}
