package com.example.loanManage.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data //Lombok: Automatically generates getters, setters, and other boilerplate methods.
@Embeddable //JPA: Marks this class as a component that will be stored as part of another entity's table.
public class Address {

    private String division;
    private String district;
    private String upazila;
}
