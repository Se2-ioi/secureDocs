package com.securedoc.domain.file.scheduler;

import com.securedoc.domain.file.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileCleanUpScheduler {

    private final FileService fileservice;

    @Scheduled(cron = "0 0 2 * * *")
    public void cleanupTrashFile() {
        fileservice.cleanUpTrashFile();
    }
}
