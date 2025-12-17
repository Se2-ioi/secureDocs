package com.securedoc.domain.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoginResponse {

    private boolean success;
    private String message;
    private Data data;

    @Getter
    @Setter
    @Builder
    public static class Data {
        private String token;
        private String refreshToken;
        private UserInfo user;
    }

    @Getter
    @Setter
    @Builder
    public static class UserInfo {
        private Long id;
        private String userId;
        private String username;
        private String email;
        private String phoneNum;
    }
}
