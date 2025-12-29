package com.example.loanManage.controller;

import com.example.loanManage.dto.LoanAccountDto;
import com.example.loanManage.entity.Borrower;
import com.example.loanManage.entity.LoanProduct;
import com.example.loanManage.service.LoanAccountService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/loan-accounts")
@CrossOrigin(origins = "*")
public class LoanAccountController {

    private final LoanAccountService loanAccountService;

    public LoanAccountController(LoanAccountService loanAccountService) {
        this.loanAccountService = loanAccountService;
    }

    // ================= CREATE =================
    @PostMapping
    public LoanAccountDto create(@RequestBody LoanAccountDto dto) {
        return loanAccountService.create(dto);
    }

    // ================= PAGED LIST =================
    @GetMapping("/page")
    public Page<LoanAccountDto> getPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size
    ) {
        return loanAccountService.getPaged(page, size);
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public LoanAccountDto getById(@PathVariable Long id) {
        return loanAccountService.getById(id);
    }

    // ================= SEARCH BORROWER =================
    @GetMapping("/search-borrower")
    public List<Borrower> searchBorrower(
            @RequestParam(name = "q", required = false) String q) {
        return loanAccountService.searchBorrower(q);
    }

    // ================= PRODUCTS =================
    @GetMapping("/products")
    public List<LoanProduct> getProducts() {
        return loanAccountService.getProducts();
    }

    // ================= NEXT LOAN NUMBER =================
    @GetMapping("/next-loan-number")
    public Map<String, String> nextLoanNumber() {
        return Map.of(
                "nextLoanNumber",
                loanAccountService.getNextLoanNumber()
        );
    }

    // ================= FIND BY LOAN NUMBER =================
    @GetMapping("/by-loan-number/{loanNumber}")
    public LoanAccountDto getByLoanNumber(
            @PathVariable String loanNumber) {
        return loanAccountService.getByLoanNumber(loanNumber);
    }
}
