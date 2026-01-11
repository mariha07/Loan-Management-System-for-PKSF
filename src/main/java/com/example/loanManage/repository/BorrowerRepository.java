package com.example.loanManage.repository;

import com.example.loanManage.dto.BorrowerDto;
import com.example.loanManage.entity.Borrower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BorrowerRepository extends JpaRepository<Borrower, Long> {
    List<Borrower> findAllByOrderByIdDesc();
    // search by idNumber (NID/BRN/Passport) or mobile containing query
    List<Borrower> findTop10ByIdNumberContainingIgnoreCaseOrMobileContainingIgnoreCase(String idNumber, String mobile);
}



