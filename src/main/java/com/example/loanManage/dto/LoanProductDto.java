package com.example.loanManage.dto;

import com.example.loanManage.entity.InstallmentType;
import com.example.loanManage.entity.LoanType;
import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;


@Getter
@Setter
 public class LoanProductDto {

    private Long id;
    private String name;
    private LoanType loanType;
    private Double interestRate;
    private Integer numberOfInstallments;
    private InstallmentType installmentType;
    private boolean active;

    private String code;
    private Double maxLoan;
    private Double minLoan;


}

