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
    @PostMapping("/generate/{loanNumber}")
    public ResponseEntity<?> generate(@PathVariable String loanNumber) {
        try {
            return ResponseEntity.ok(service.generateSchedule(loanNumber));
        } catch (ResponseStatusException ex) {
            return ResponseEntity
                    .status(ex.getStatusCode())
                    .body(ex.getReason());
        }
    }


    @GetMapping("/{loanNumber}")
    public List<RepaymentScheduleDto> getByLoanNumber(
            @PathVariable String loanNumber) {
        return service.getByLoanNumber(loanNumber);
    }

}
