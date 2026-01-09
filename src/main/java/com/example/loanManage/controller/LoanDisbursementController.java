package com.example.loanManage.controller;

import com.example.loanManage.dto.LoanDisbursementDto;
import com.example.loanManage.service.LoanDisbursementService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Marks this as a REST API controller that returns data in JSON format.
@RequestMapping("/api/disbursements") // Sets the base URL path for all money disbursement actions.
@CrossOrigin("*") // Allows any external frontend application to connect to these endpoints.
public class LoanDisbursementController {

    private final LoanDisbursementService service; // Reference to the service that handles the "money release" logic.

    public LoanDisbursementController(LoanDisbursementService service) { // Constructor that connects the service to this controller.
        this.service = service;
    }

    @GetMapping("/search") // Handles GET requests to find a loan that is ready to be paid out.
    public LoanDisbursementDto search(@RequestParam String loanNumber) {
        // @RequestParam gets the loan number from the URL (e.g., ?loanNumber=L-101).
        return service.findByLoanNumber(loanNumber); // Asks service to find specific loan details for disbursement.
    }

    @PostMapping // Handles POST requests to confirm and process the payment to the borrower.
    public LoanDisbursementDto disburse(@RequestBody LoanDisbursementDto dto) {
        // @RequestBody takes the disbursement details (date, amount, method) from the frontend.
        return service.disburse(dto); // Tells the service to mark the loan as "paid/disbursed" in the database.
    }

    @GetMapping // Handles GET requests to see a list of all historical loan payments.
    public List<LoanDisbursementDto> getAll() {
        return service.getAll(); // Returns a list of all successful disbursements.
    }
}
