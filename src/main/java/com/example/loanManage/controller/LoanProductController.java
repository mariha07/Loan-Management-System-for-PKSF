package com.example.loanManage.controller;

import com.example.loanManage.dto.LoanProductDto;
import com.example.loanManage.service.LoanProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.io.ByteArrayInputStream;

import java.util.List;

@RestController // Tells Spring that this class is a REST API that handles data in JSON format.
@RequestMapping("/api/loan-products") // Sets the base web address for all loan product operations.
@CrossOrigin(origins = "*") // Allows any frontend (like React) to connect to these endpoints.
public class LoanProductController {

    @Autowired
    private LoanProductService loanProductService;

    // POST /api/loan-products
    @PostMapping // Handles POST requests to save a new loan product (e.g., "Gold Loan").
    public ResponseEntity<LoanProductDto> create(@RequestBody LoanProductDto request) {
        LoanProductDto created = loanProductService.create(request); // Asks the service to save the product.
        return ResponseEntity.ok(created); // Returns the saved product with a 200 OK status.
    }

    // GET /api/loan-products
    @GetMapping // Handles GET requests to retrieve every loan product in the database.
    public ResponseEntity<List<LoanProductDto>> getAll() {
        List<LoanProductDto> products = loanProductService.getAll();
        return ResponseEntity.ok(products);
    }

    // GET /api/loan-products/active
    @GetMapping("/active") // Fetches only the products that are currently "Live" or available for customers.
    public ResponseEntity<List<LoanProductDto>> getActive() {
        List<LoanProductDto> products = loanProductService.getActive();
        return ResponseEntity.ok(products);
    }

    // GET /api/loan-products/{id}
    @GetMapping("/{id}") // Fetches details for one specific product using its ID (e.g., /api/loan-products/1).
    public ResponseEntity<LoanProductDto> getById(@PathVariable Long id) {
        LoanProductDto product = loanProductService.getById(id);
        return ResponseEntity.ok(product);
    }

    // PUT /api/loan-products/{id}
    @PutMapping("/{id}") // Handles UPDATE requests to change product details (like changing the interest rate).
    public ResponseEntity<LoanProductDto> update(@PathVariable Long id, @RequestBody LoanProductDto request) {
        LoanProductDto updated = loanProductService.update(id, request);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/loan-products/{id}
    @DeleteMapping("/{id}") // Soft-deletes a product by marking it as "inactive" instead of removing it from the DB.
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        loanProductService.deactivate(id); // Calls the service to flip the active status to false.
        return ResponseEntity.noContent().build(); // Returns a 204 "No Content" status (successful deletion).
    }

    @GetMapping("/report/pdf")
    public ResponseEntity<InputStreamResource> downloadPdf() {
        ByteArrayInputStream bis = loanProductService.generateLoanProductReport();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=loan_products.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }
}