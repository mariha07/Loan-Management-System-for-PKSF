package com.example.loanManage.controller;

import com.example.loanManage.dto.RepaymentScheduleDto;
import com.example.loanManage.service.RepaymentScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
@RestController // Marks this class as a REST API controller that returns data (JSON).
@RequestMapping("/api/repayment-schedules") // Sets the base URL for all installment/schedule tasks.
@CrossOrigin("*") // Allows any frontend application to access these specific endpoints.
public class RepaymentScheduleController {

    private final RepaymentScheduleService service; // Reference to the service that calculates the math for installments.

    public RepaymentScheduleController(RepaymentScheduleService service) { // Connects the service via constructor injection.
        this.service = service;
    }

    @PostMapping("/generate/{loanNumber}") // Handles requests to calculate a new schedule for a specific loan.
    public ResponseEntity<?> generate(@PathVariable String loanNumber) {
        // @PathVariable extracts the loan number from the URL (e.g., /generate/LOAN-101).

        try {
            // Asks the service to create the list of payment dates and amounts.
            return ResponseEntity.ok(service.generateSchedule(loanNumber));
        } catch (ResponseStatusException ex) {
            // If something goes wrong (e.g., loan not found), catch the specific error.
            return ResponseEntity
                    .status(ex.getStatusCode()) // Return the correct error code (like 404).
                    .body(ex.getReason()); // Return the error message (like "Loan not found").
        }
    }

    @GetMapping("/by-loan/{loanNumber}") // Handles requests to view an existing payment schedule.
    public List<RepaymentScheduleDto> getByLoanNumber(@PathVariable String loanNumber) {
        // Returns the list of all installments (dates, principal, interest) for that loan.
        return service.getByLoanNumber(loanNumber);
    }
}
