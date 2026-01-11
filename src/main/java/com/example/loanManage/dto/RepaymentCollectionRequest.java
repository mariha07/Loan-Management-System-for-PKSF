package com.example.loanManage.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RepaymentCollectionRequest {
    private String loanNumber;
    private Integer installmentNo;
    private BigDecimal paymentAmount;
}
