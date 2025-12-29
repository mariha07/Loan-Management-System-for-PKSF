package com.example.loanManage.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class LoanDisbursementDto {

    private Long id;

    // MUST MATCH FRONTEND
    private Long loanAccountId;

    private String loanNumber;
    private String borrowerName;
    private String loanProductName;

    private BigDecimal approvedAmount;
    private BigDecimal disbursementAmount;
}
