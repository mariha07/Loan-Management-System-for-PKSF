package com.example.loanManage.controller;

import com.example.loanManage.dto.LoanAccountDto;
import com.example.loanManage.entity.Borrower;
import com.example.loanManage.entity.LoanProduct;
import com.example.loanManage.service.LoanAccountService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController // Marks this class as a REST controller that handles requests and returns JSON.
@RequestMapping("/api/loan-accounts") // Defines the base URL for all loan account operations.
@CrossOrigin(origins = "*") // Allows your frontend application to access these APIs from a different port.
public class LoanAccountController {

    private final LoanAccountService loanAccountService; // Reference to the service layer for loan logic.

    public LoanAccountController(LoanAccountService loanAccountService) { // Connects the service via constructor injection.
        this.loanAccountService = loanAccountService;
    }

    // ================= CREATE =================
    @PostMapping // Handles POST requests to create a new loan account in the system.
    public LoanAccountDto create(@RequestBody LoanAccountDto dto) {
        return loanAccountService.create(dto); // Sends the data to the service and returns the saved loan details.
    }

    // ================= PAGED LIST =================
    @GetMapping("/page") // Fetches loan accounts in chunks (pages) to keep the app fast.
    public Page<LoanAccountDto> getPaged(
            @RequestParam(defaultValue = "0") int page, // The page number (starts at 0).
            @RequestParam(defaultValue = "8") int size // How many loans to show per page.
    ) {
        return loanAccountService.getPaged(page, size);
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}") // Fetches a specific loan's details using its database primary key.
    public LoanAccountDto getById(@PathVariable Long id) {
        return loanAccountService.getById(id);
    }

    // ================= SEARCH BORROWER =================
    @GetMapping("/search-borrower") // Used in a search bar to find people who want a loan.
    public List<Borrower> searchBorrower(@RequestParam(name = "q", required = false) String q) {
        // 'q' is the search keyword (like name or phone number).
        return loanAccountService.searchBorrower(q);
    }

    // ================= PRODUCTS =================
    @GetMapping("/products") // Fetches types of loans available (e.g., Home Loan, Car Loan).
    public List<LoanProduct> getProducts() {
        return loanAccountService.getProducts();
    }

    // ================= NEXT LOAN NUMBER =================
    @GetMapping("/next-loan-number") // Automatically generates a new, unique ID for a loan (e.g., LOAN-1001).
    public Map<String, String> nextLoanNumber() {
        return Map.of("nextLoanNumber", loanAccountService.getNextLoanNumber());
        // Returns the number inside a JSON object for the frontend to display.
    }

    // ================= FIND BY LOAN NUMBER =================
    @GetMapping("/by-loan-number/{loanNumber}") // Finds a loan using its human-readable number (not ID).
    public LoanAccountDto getByLoanNumber(@PathVariable String loanNumber) {
        return loanAccountService.getByLoanNumber(loanNumber);
    }
}