package com.example.loanManage.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "loan_disbursement")
public class LoanDisbursement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "loan_account_id")
    private LoanAccount loanAccount;

    @Column(nullable = false)
    private BigDecimal disbursementAmount;

    @Column(nullable = false)
    private String status;   // REQUIRED (fix SQL error)


}
