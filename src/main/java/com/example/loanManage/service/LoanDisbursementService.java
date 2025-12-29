package com.example.loanManage.service;

import com.example.loanManage.dto.LoanDisbursementDto;

import java.util.List;

public interface LoanDisbursementService {

    LoanDisbursementDto disburse(LoanDisbursementDto dto);

    LoanDisbursementDto findByLoanNumber(String loanNumber);

    // MATCH CONTROLLER
    List<LoanDisbursementDto> getAll();
}
