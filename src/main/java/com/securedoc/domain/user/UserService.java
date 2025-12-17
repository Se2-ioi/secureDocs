package com.securedoc.domain.user;

import com.securedoc.domain.user.dto.LoginRequest;
import com.securedoc.domain.user.dto.LoginResponse;

public interface UserService {

    void join(User user);
    User findUser(Long id);

    LoginResponse login(LoginRequest request);
    boolean existsByUserId(String userId);
    boolean existsByEmail(String email);
}
