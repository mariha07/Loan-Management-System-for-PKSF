package com.example.loanManage.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "loan_products")
public class LoanProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Name of the loan product
    @Column(nullable = false)
    private String name;

    // PERSONAL / BUSINESS
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanType loanType;

    // Interest rate percentage, e.g. 10 = 10%
    @Column(nullable = false)
    private Double interestRate;

    // 12 / 24 / 36 etc.
    @Column(nullable = false)
    private Integer numberOfInstallments;

    // WEEKLY / MONTHLY / YEARLY
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InstallmentType installmentType;

    // for activate/deactivate product
    private boolean active = true;

    public LoanProduct() {}
    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private Double maxLoan;

    @Column(nullable = false)
    private Double minLoan;


}
