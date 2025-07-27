package com.example.demo.auth.dto;

import com.example.demo.auth.entity.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    public String username;
    public String password;
    public String email;
    public Role role = Role.GUEST;

}
