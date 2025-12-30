package com.securedoc.domain.link;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FileLinkRepository extends JpaRepository<FileLink, Long> {

    Optional<FileLink> findByShareUuid(String shareUuid);
    // 공유 목록이 여러개 -> 리스트
    List<FileLink> findByUserId(Long userId);

    // 링크 다운로드 시 필요?
    Optional<FileLink> findByExpireDateBefore(LocalDateTime currentTime);

}
