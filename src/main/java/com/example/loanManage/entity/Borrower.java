package com.example.loanManage.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@Entity
public class Borrower {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private IdType idType;

    private String idNumber;

    private String dateOfBirth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String mobile;
    private String email;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "division", column = @Column(name = "present_division")),
            @AttributeOverride(name = "district", column = @Column(name = "present_district")),
            @AttributeOverride(name = "upazila", column = @Column(name = "present_upazila")),
            @AttributeOverride(name = "street", column = @Column(name = "present_street"))
    })
    private Address presentAddress;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "division", column = @Column(name = "permanent_division")),
            @AttributeOverride(name = "district", column = @Column(name = "permanent_district")),
            @AttributeOverride(name = "upazila", column = @Column(name = "permanent_upazila")),
            @AttributeOverride(name = "street", column = @Column(name = "permanent_street"))
    })
    private Address permanentAddress;

}
