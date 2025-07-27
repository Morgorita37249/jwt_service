package com.example.demo.auth.service;

import com.example.demo.auth.dto.*;
import com.example.demo.auth.entity.*;
import com.example.demo.auth.repository.*;
import com.example.demo.auth.security.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepo;
    private final TokenRepository tokenRepo;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepo, TokenRepository tokenRepo,
                       JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.tokenRepo = tokenRepo;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse register(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.getRoles().add(request.getRole() != null ? request.getRole() : Role.GUEST);
        userRepo.save(user);

        String accessToken = jwtService.generateToken(user);

        saveToken(user, accessToken, false);

        return new AuthResponse(accessToken);
    }


    public AuthResponse login(LoginRequest request) {
        User user = userRepo.findByUsername(request.username)
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password, user.getPassword()))
            throw new BadCredentialsException("Invalid credentials");

        String accessToken = jwtService.generateToken(user);

        saveToken(user, accessToken, false);
        return new AuthResponse(accessToken);
    }


    public void logout(String token) {
        Token t = tokenRepo.findByToken(token).orElseThrow();
        t.setRevoked(true);
        tokenRepo.save(t);
    }

    private void saveToken(User user, String jwt, boolean isRefresh) {
        Token token = new Token();
        token.setUser(user);
        token.setToken(jwt);
        token.setRevoked(false);
        token.setRefresh(isRefresh);
        token.setExpiryDate(Instant.now().plus(isRefresh ? 7 : 0, ChronoUnit.DAYS));
        tokenRepo.save(token);
    }
    public Optional<Token> getValidRefreshToken(String refreshToken) {
        return tokenRepo.findByToken(refreshToken)
                .filter(Token::isRefresh)
                .filter(t -> !t.isRevoked());
    }

    public void saveAccessToken(User user, String token) {
        saveToken(user, token, false);
    }

    public User findUser(String username) {
        return userRepo.findByUsername(username).orElseThrow();
    }
    public void saveRefreshToken(User user, String token) {
        saveToken(user, token, true);
    }
    public void revokeToken(Token token) {
        token.setRevoked(true);
        tokenRepo.save(token);
    }
}
