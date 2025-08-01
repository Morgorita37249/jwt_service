package com.example.demo.auth.entity;


import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ADMIN,
    PREMIUM_USER,
    GUEST;

    @Override
    public String getAuthority() {
        return name();
    }
}
