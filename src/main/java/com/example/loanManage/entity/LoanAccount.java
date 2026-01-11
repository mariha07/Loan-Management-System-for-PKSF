package com.example.loanManage.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "loan_account")
public class LoanAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Auto-generated LN-00001 style
    @Column(name = "loan_number", unique = true, length = 20)
    private String loanNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrower_id", nullable = false)
    private Borrower borrower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_product_id", nullable = false)
    private LoanProduct loanProduct;

    @Column(name = "approved_amount", nullable = false)
    private BigDecimal approvedAmount;
    private String loanType; // Added to match LoanProduct
    private Integer numberOfInstallments; // Added to match LoanProduct
    private String installmentType;

    // Acts as DISBURSEMENT DATE / START DATE for repayment
    @Column(name = "opening_date", nullable = false)
    private LocalDate openingDate;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "disbursement_date", nullable = false)
    private LocalDate disbursementDate;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }
}
