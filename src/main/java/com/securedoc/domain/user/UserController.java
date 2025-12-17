package com.securedoc.domain.user;

import com.securedoc.domain.user.dto.LoginRequest;
import com.securedoc.domain.user.dto.LoginResponse;
import com.securedoc.domain.user.dto.UserCreateRequest;
import com.securedoc.domain.user.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(
            @Valid @RequestBody UserCreateRequest request) {

        try {
            User user = request.toEntity();
            userService.join(user);

            UserResponse response = new UserResponse();
            response.setId(user.getId());
            response.setUserId(user.getUserId());
            response.setUsername(user.getUsername());
            response.setEmail(user.getEmail());
            response.setPhoneNum(user.getPhoneNum());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalStateException e) {
            // 중복 에러
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            // 서비스에서 기존 LoginResponse(토큰 + 유저 정보 직접 포함된 형태)를 받음
            LoginResponse raw = userService.login(request);

            LoginResponse wrapped = LoginResponse.builder()
                    .success(raw.isSuccess())
                    .message(raw.getMessage())
                    .data(
                            LoginResponse.Data.builder()
                                    .token(raw.getData().getToken())
                                    .refreshToken(raw.getData().getRefreshToken())
                                    .user(raw.getData().getUser())
                                    .build()
                    )
                    .build();

            return ResponseEntity.ok(wrapped);

        } catch (IllegalStateException | IllegalArgumentException e) {

            LoginResponse fail = LoginResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .data(null)
                    .build();

            return ResponseEntity.status(401).body(fail);
        }
    }

    @GetMapping("/check-userid")
    public ResponseEntity<?> checkUserId(@RequestParam String userId) {

        boolean exists = userService.existsByUserId(userId);

        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("available", !exists);

        if(exists) {
            body.put("message", "이미 사용 중인 아이디입니다.");
        }

        return ResponseEntity.ok(body);
    }

    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {

        boolean exists = userService.existsByEmail(email);

        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("available", !exists);

        if(exists) {
            body.put("message", "이미 사용 중인 이메일입니다.");
        }

        return ResponseEntity.ok(body);
    }
}
