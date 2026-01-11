package com.example.loanManage.controller;

import com.example.loanManage.dto.RepaymentScheduleDto;
import com.example.loanManage.service.RepaymentScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

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
    @GetMapping("/by-loan/{loanNumber}")
    public List<RepaymentScheduleDto> getByLoanNumber(
            @PathVariable String loanNumber) {

        return service.getByLoanNumber(loanNumber);
    }
}
