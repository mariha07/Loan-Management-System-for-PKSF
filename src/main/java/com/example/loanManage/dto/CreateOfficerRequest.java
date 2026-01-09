package com.example.loanManage.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOfficerRequest {

    private String name;
    private String email;
    private String phone;
    private String password;
    private String role; // "LOAN_OFFICER" or "COLLECTION_OFFICER"
}
