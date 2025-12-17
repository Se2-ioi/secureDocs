package com.securedoc.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {

    private Long id;
    private String userId;
    private String username;
    private String email;
    private String phoneNum;
}
