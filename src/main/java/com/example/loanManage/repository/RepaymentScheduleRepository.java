package com.example.loanManage.repository;

import com.example.loanManage.entity.LoanAccount;
import com.example.loanManage.entity.RepaymentSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepaymentScheduleRepository
        extends JpaRepository<RepaymentSchedule, Long> {


    List<RepaymentSchedule> findByLoanAccount_LoanNumber(String loanNumber);
    boolean existsByLoanAccount_LoanNumber(String loanNumber);
    void deleteByLoanAccount_LoanNumber(String loanNumber);

    List<RepaymentSchedule>
    findByLoanAccount_LoanNumberOrderByInstallmentNoAsc(String loanNumber);
    Optional<RepaymentSchedule>
    findTopByLoanAccountOrderByInstallmentNoDesc(LoanAccount loanAccount);

}
