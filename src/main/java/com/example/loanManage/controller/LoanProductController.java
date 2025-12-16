package com.example.loanManage.controller;

import com.example.loanManage.dto.LoanProductDto;
import com.example.loanManage.service.LoanProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loan-products")
@CrossOrigin(origins = "*")
public class LoanProductController {

    private final LoanProductService loanProductService;

    public LoanProductController(LoanProductService loanProductService) {
        this.loanProductService = loanProductService;
    }

    // POST /api/loan-products
    @PostMapping
    public ResponseEntity<LoanProductDto> create(@RequestBody LoanProductDto request) {
        LoanProductDto created = loanProductService.create(request);
        return ResponseEntity.ok(created);
    }

    // GET /api/loan-products
    @GetMapping
    public ResponseEntity<List<LoanProductDto>> getAll() {
        List<LoanProductDto> products = loanProductService.getAll();
        return ResponseEntity.ok(products);
    }

    // GET /api/loan-products/active
    @GetMapping("/active")
    public ResponseEntity<List<LoanProductDto>> getActive() {
        List<LoanProductDto> products = loanProductService.getActive();
        return ResponseEntity.ok(products);
    }

    // GET /api/loan-products/{id}
    @GetMapping("/{id}")
    public ResponseEntity<LoanProductDto> getById(@PathVariable Long id) {
        LoanProductDto product = loanProductService.getById(id);
        return ResponseEntity.ok(product);
    }

    // PUT /api/loan-products/{id}
    @PutMapping("/{id}")
    public ResponseEntity<LoanProductDto> update(
            @PathVariable Long id,
            @RequestBody LoanProductDto request) {

        LoanProductDto updated = loanProductService.update(id, request);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/loan-products/{id}  -> mark inactive
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        loanProductService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
