package com.example.loanManage.service;

import com.example.loanManage.dto.LoanProductDto;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface LoanProductService {

    LoanProductDto create(LoanProductDto request);

    List<LoanProductDto> getAll();

    List<LoanProductDto> getActive();

    LoanProductDto getById(Long id);

    LoanProductDto update(Long id, LoanProductDto request);

    void deactivate(Long id);

    ByteArrayInputStream generateLoanProductReport();
}
