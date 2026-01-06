package com.example.loanManage.service;

import com.example.loanManage.dto.LoanAccountDto;
import com.example.loanManage.entity.Borrower;
import com.example.loanManage.entity.LoanProduct;
import org.springframework.data.domain.Page;

import java.util.List;

public interface LoanAccountService {

    LoanAccountDto create(LoanAccountDto dto);

    Page<LoanAccountDto> getPaged(int page, int size);

    LoanAccountDto getById(Long id);

    List<Borrower> searchBorrower(String q);

    List<LoanProduct> getProducts();

    String getNextLoanNumber();

    LoanAccountDto getByLoanNumber(String loanNumber);

}
