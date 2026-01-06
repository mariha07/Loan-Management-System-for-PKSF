package com.example.loanManage.dto;

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


}
