package com.example.loanManage.service;

import com.example.loanManage.dto.LoanAccountDto;

import java.util.List;
import org.springframework.data.domain.Page;

public interface LoanAccountService {
    String getNextLoanNumber();
    LoanAccountDto create(LoanAccountDto dto);

    Page<LoanAccountDto> getPaged(int page, int size);

    LoanAccountDto getById(Long id);
}
