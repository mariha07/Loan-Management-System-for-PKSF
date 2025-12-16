package com.example.loanManage.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class Address {

    private String division;
    private String district;
    private String upazila;
}
