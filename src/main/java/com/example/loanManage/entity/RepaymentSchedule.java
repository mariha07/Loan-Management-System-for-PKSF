package com.example.loanManage.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
@Getter
@Setter
@Entity
@Table(name = "repayment_schedule")
public class RepaymentSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "loan_account_id", nullable = false)
    private LoanAccount loanAccount;

    private Integer installmentNo;

    private LocalDate repaymentDate;

    private BigDecimal principalOutstanding;

    private BigDecimal interestOutstanding;




}
