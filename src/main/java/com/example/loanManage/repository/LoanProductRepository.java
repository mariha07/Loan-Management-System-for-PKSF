package com.example.loanManage.repository;

import com.example.loanManage.entity.LoanProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanProductRepository extends JpaRepository<LoanProduct, Long> {

    List<LoanProduct> findByActiveTrue();
}
