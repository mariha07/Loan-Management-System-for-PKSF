package com.example.loanManage.controller;

import com.example.loanManage.dto.RepaymentCollectionRequest;
import com.example.loanManage.dto.RepaymentScheduleDto;
import com.example.loanManage.service.RepaymentScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/repayment-schedules")
@CrossOrigin("*")
public class RepaymentScheduleController {

    private final RepaymentScheduleService service;

    public RepaymentScheduleController(RepaymentScheduleService service) {
        this.service = service;
    }

    @PostMapping("/generate-all/{loanNumber}")
    public List<RepaymentScheduleDto> generateAll(@PathVariable String loanNumber) {
        return service.generateAllInstallments(loanNumber);
    }

    @PostMapping("/pay-next/{loanNumber}")
    public ResponseEntity<?> payAndGenerateNext(
            @PathVariable String loanNumber) {

        try {
            RepaymentScheduleDto dto =
                    service.payAndGenerateNext(loanNumber);
            return ResponseEntity.ok(dto);

        } catch (ResponseStatusException ex) {
            return ResponseEntity
                    .status(ex.getStatusCode())
                    .body(ex.getReason());
        }
    }

    // ==================================================
    // 3️⃣ VIEW ALL GENERATED INSTALLMENTS (HISTORY)
    // ==================================================
    @GetMapping({"/by-loan/{loanNumber}", "/loan/{loanNumber}"})
    public List<RepaymentScheduleDto> getByLoan(@PathVariable String loanNumber) {
        return service.getSchedulesByLoanNumber(loanNumber);
    }

    @PostMapping("/collect")
    public ResponseEntity<?> collect(@RequestBody RepaymentCollectionRequest request) {
        service.collectPayment(request);
        return ResponseEntity.ok("Payment successful");
    }
}
