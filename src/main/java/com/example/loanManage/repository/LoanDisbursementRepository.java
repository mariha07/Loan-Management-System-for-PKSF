package com.example.loanManage.repository;

import com.example.loanManage.entity.LoanAccount;
import com.example.loanManage.entity.LoanDisbursement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanDisbursementRepository
        extends JpaRepository<LoanDisbursement, Long> {
    Optional<LoanDisbursement> findTopByLoanAccountOrderByIdDesc(LoanAccount loanAccount);

    List<LoanDisbursement> findByLoanAccountId(Long loanAccountId);
}
