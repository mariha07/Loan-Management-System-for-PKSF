package com.example.loanManage.repository;

import com.example.loanManage.entity.LoanAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoanAccountRepository extends JpaRepository<LoanAccount, Long> {

    // find latest by id (fast & simple)
    Optional<LoanAccount> findTopByOrderByIdDesc();

    // alternatively if you want by loanNumber ordering
    @Query("select la from LoanAccount la where la.loanNumber is not null order by la.id desc")
    Optional<LoanAccount> findLatestWithLoanNumber();
    Optional<LoanAccount> findByLoanNumber(String loanNumber);

}
