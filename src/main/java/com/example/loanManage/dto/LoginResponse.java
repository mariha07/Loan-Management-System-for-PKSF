package com.example.loanManage.dto;

import com.example.loanManage.entity.UserRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private UserRole role;
    private boolean success;
    private String message;
}
