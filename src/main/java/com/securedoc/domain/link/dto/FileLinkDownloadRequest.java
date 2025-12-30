package com.securedoc.domain.link.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class FileLinkDownloadRequest {

    @Pattern(regexp = "^\\d{4}$", message = "PIN은 4자리 숫자여야 합니다.")
    private String pin;

}
