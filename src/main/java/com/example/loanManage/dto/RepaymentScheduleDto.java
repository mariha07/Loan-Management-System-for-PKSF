package com.example.loanManage.dto;

import com.example.loanManage.entity.LoanType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RepaymentScheduleDto {

    private Long id;
    private String loanNumber;
    private Integer installmentNo;
    private String repaymentDate;

    private BigDecimal principalOutstanding;
    private BigDecimal interestOutstanding;
    private BigDecimal installmentAmount;;

    private BigDecimal principalPaid;
    private BigDecimal interestPaid;
    private String status;

}
