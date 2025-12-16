package com.example.loanManage.controller;

import com.example.loanManage.dto.LoanAccountDto;
import com.example.loanManage.entity.Borrower;
import com.example.loanManage.entity.LoanProduct;
import com.example.loanManage.repository.BorrowerRepository;
import com.example.loanManage.repository.LoanProductRepository;
import com.example.loanManage.service.LoanAccountService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
@RestController
@RequestMapping("/api/loan-accounts")
@CrossOrigin(origins = "*")
public class LoanAccountController {

    private final LoanAccountService loanAccountService;
    private final BorrowerRepository borrowerRepository;
    private final LoanProductRepository loanProductRepository;

    public LoanAccountController(LoanAccountService loanAccountService,
                                 BorrowerRepository borrowerRepository,
                                 LoanProductRepository loanProductRepository) {
        this.loanAccountService = loanAccountService;
        this.borrowerRepository = borrowerRepository;
        this.loanProductRepository = loanProductRepository;
    }

    // --------- Create ----------
    @PostMapping
    public LoanAccountDto create(@RequestBody LoanAccountDto dto) {
        return loanAccountService.create(dto);
    }

    // --------- List ----------

    @GetMapping("/page")
    public Page<LoanAccountDto> getPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return loanAccountService.getPaged(page, size);
    }


    // --------- Single ----------
    @GetMapping("/{id}")
    public LoanAccountDto getById(@PathVariable Long id) {
        return loanAccountService.getById(id);
    }

    // --------- Borrower search used by UI ----------
    // q is optional in the signature (safer); controller will return empty list if q is missing/empty
    @GetMapping("/search-borrower")
    public List<Borrower> searchBorrower(@RequestParam(name = "q", required = false) String q) {
        if (q == null || q.trim().isEmpty()) {
            return List.of(); // return empty list if no query provided
        }
        return borrowerRepository.findTop10ByIdNumberContainingIgnoreCaseOrMobileContainingIgnoreCase(q, q);
    }

    // --------- Products list for dropdown ----------
    @GetMapping("/products")
    public List<LoanProduct> getProducts() {
        return loanProductRepository.findAll();
    }

    @GetMapping("/next-loan-number")
    public Map<String, String> nextLoanNumber() {
        return Map.of("nextLoanNumber", loanAccountService.getNextLoanNumber());
    }

}
