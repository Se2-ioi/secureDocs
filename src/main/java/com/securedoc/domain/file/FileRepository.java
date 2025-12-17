package com.securedoc.domain.file;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    // 쿼리 자동 생성
    Page<File> findByUserIdAndDeleteDateIsNull(Long userId, Pageable pageable);

}
