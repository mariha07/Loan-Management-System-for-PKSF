package com.example.loanManage.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class LoanAccountDto {

    private Long id;

    // Loan number (LN-00001 format)
    private String loanNumber;

    // Borrower (request + response)
    private Long borrowerId;            // MUST BE Long for POST
    private String borrowerName;        // response
    private String borrowerIdType;      // response
    private String borrowerIdNumber;    // response
    private String borrowerMobile;      // response

    // Loan Product (request + response)
    private Long loanProductId;         // MUST BE Long for POST
    private String loanProductName;     // response only
    private BigDecimal interestRate;    // response
    private String installmentType;     // response
    private Integer numberOfInstallments;

    // Account info
    private BigDecimal approvedAmount;
    private String openingDate;         // yyyy-MM-dd
    private String status;
}
