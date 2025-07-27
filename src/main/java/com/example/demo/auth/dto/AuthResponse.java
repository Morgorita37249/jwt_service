package com.example.demo.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL) // <-- вот это ключ
public class AuthResponse {
    private final String token;
    private final String refreshToken;

    public AuthResponse(String token, String refreshToken) {
        this.token = token;
        this.refreshToken = refreshToken;
    }

    public AuthResponse(String token) {
        this(token, null); // делегирует в основной конструктор
    }
}

