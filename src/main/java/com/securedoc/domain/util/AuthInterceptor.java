package com.securedoc.domain.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // Authorization Header 가져오기
        String authorization = request.getHeader("Authorization");

        // 디버깅 로그
        System.out.println("받은 헤더: [" + authorization + "]");

        // 토큰이 없는 경우
        if (authorization == null || authorization.isEmpty()) {
            throw new IllegalArgumentException("인증 토큰이 제공되지 않았습니다.");
        }

        // "Bearer " 체크 후 제거  ← 여기 수정!
        if (!authorization.startsWith("Bearer ")) {
            throw new IllegalArgumentException("잘못된 토큰 형식입니다.");
        }
        String token = authorization.substring(7);

        // 디버깅 로그
        System.out.println("추출한 토큰: [" + token + "]");

        jwtUtil.validateToken(token);

        String userId = jwtUtil.getUserIdFromToken(token);
        request.setAttribute("userId", userId);

        return true;
    }
}