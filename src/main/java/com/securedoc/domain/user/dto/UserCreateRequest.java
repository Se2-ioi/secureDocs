package com.securedoc.domain.user.dto;

import com.securedoc.domain.user.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateRequest {

    private String userId;
    private String password;
    private String username;
    private String email;
    private String phoneNum;

    public User toEntity() {
        return User.builder()
                .userId(this.userId)
                .password(this.password)
                .username(this.username)
                .email(this.email)
                .phoneNum(this.phoneNum)
                .build();
    }

}
