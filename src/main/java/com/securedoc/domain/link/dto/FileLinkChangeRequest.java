package com.securedoc.domain.link.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class FileLinkChangeRequest {

    @Pattern(regexp = "^\\d{4}$", message = "PIN은 4자리 숫자여야 합니다.")
    private String pin;

    @NotNull(message = "공유 기한을 선택해주세요.")
    @Pattern(regexp = "^(1h|24h|7d)$", message = "공유 기한은 1시간, 24시간, 7일 중 하나여야 합니다.")
    private String expires;
}
