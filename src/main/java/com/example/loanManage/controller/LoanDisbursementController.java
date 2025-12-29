package com.example.loanManage.controller;

import com.example.loanManage.dto.LoanDisbursementDto;
import com.example.loanManage.service.LoanDisbursementService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/disbursements")
@CrossOrigin("*")
public class LoanDisbursementController {

    private final LoanDisbursementService service;

    public LoanDisbursementController(LoanDisbursementService service) {
        this.service = service;
    }

    @GetMapping("/search")
    public LoanDisbursementDto search(@RequestParam String loanNumber) {
        return service.findByLoanNumber(loanNumber);
    }

    @PostMapping
    public LoanDisbursementDto disburse(@RequestBody LoanDisbursementDto dto) {
        return service.disburse(dto);
    }

    @GetMapping
    public List<LoanDisbursementDto> getAll() {
        return service.getAll();
    }

}
