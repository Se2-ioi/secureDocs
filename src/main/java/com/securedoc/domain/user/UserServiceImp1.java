package com.securedoc.domain.user;

import com.securedoc.domain.user.dto.LoginRequest;
import com.securedoc.domain.user.dto.LoginResponse;
import com.securedoc.domain.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImp1 implements UserService{

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEnc;

    public UserServiceImp1(UserRepository userRepository, PasswordEncoder passwordEnc, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEnc = passwordEnc;
    }

    @Override
    public void join(User user) {
        if (existsByUserId(user.getUserId())) {
            throw new IllegalStateException("이미 가입된 아이디입니다.");
        }

        if (existsByEmail(user.getEmail())) {
            throw new IllegalStateException("이미 가입된 이메일입니다.");
        }
        // 비밀번호 암호화
        user.setPassword(passwordEnc.encode(user.getPassword()));
        user.setRole(Role.USER);
        user.setAccountStatus(1);

        userRepository.save(user);
    }

    @Override
    public User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("회원 내역을 찾을 수 없습니다."));
    }

    @Override
    public boolean existsByUserId(String userId) {
        return userRepository.findByUserId(userId).isPresent();
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public LoginResponse login(LoginRequest request) {

        // 아이디 확인
        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 틀렸습니다."));

        // 비활성화 계정(0) 체크
        if (user.getAccountStatus() == 0) {
            throw new IllegalStateException("로그인이 차단된 계정입니다.");
        }

        // 비밀번호 확인 (BCrypt)
        if (!passwordEnc.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 틀렸습니다.");
        }

        // JWT Token 생성
        String userId = request.getUserId();
        String accessToken = jwtUtil.createAccessToken(user.getId().toString());
        String refreshToken = jwtUtil.createRefreshToken(user.getId().toString());

        // Response
        return LoginResponse.builder()
                .success(true)
                .message("로그인 성공")
                .data(
                        LoginResponse.Data.builder()
                                .token(accessToken)
                                .refreshToken(refreshToken)
                                .user(
                                        LoginResponse.UserInfo.builder()
                                                .id(user.getId())
                                                .userId(user.getUserId())
                                                .username(user.getUsername())
                                                .email(user.getEmail())
                                                .phoneNum(user.getPhoneNum())
                                                .build()
                                )
                                .build()
                )
                .build();
    }

}
