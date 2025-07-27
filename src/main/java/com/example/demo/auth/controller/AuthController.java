package com.example.demo.auth.controller;

import com.example.demo.auth.dto.*;
import com.example.demo.auth.entity.Token;
import com.example.demo.auth.entity.User;
import com.example.demo.auth.security.JwtService;
import com.example.demo.auth.service.AuthService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService service;
    private final JwtService jwtService;
    public AuthController(AuthService service, JwtService jwtService) {
        this.service = service;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(service.login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String header) {
        String token = header.replace("Bearer ", "");
        service.logout(token);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestHeader("Authorization") String header) {
        String refreshToken = header.replace("Bearer ", "");
        String username = jwtService.extractUsername(refreshToken);
        User user = service.findUser(username);

        Token stored = service.getValidRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token invalid"));

        if (!jwtService.isTokenValid(refreshToken, user))
            throw new RuntimeException("Refresh token expired");

        stored.setRevoked(true);
        service.revokeToken(stored);

        String newAccessToken = jwtService.generateToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);


        service.saveAccessToken(user, newAccessToken);
        service.saveRefreshToken(user, newRefreshToken);

        return ResponseEntity.ok(new AuthResponse(newAccessToken, newRefreshToken));
    }

}
