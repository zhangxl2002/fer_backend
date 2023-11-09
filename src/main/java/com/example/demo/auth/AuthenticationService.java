package com.example.demo.auth;

import com.example.demo.config.JwtService;

import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import com.example.demo.user.UserRole;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                //密码存储编码后的形式
                .password(passwordEncoder.encode(request.getPassword()))
                //只支持普通用户
                .userRole(UserRole.USER)
                .build();
        repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .userId(user.getId())
                .name(user.getName())
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // ??? 这一步应该是用于验证email和密码是否匹配，不匹配抛出异常，匹配则更新上下文
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .userId(user.getId())
                .name(user.getName())
                .build();
    }
}
