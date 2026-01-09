package com.example.loanManage.controller;

import com.example.loanManage.dto.BorrowerDto;
import com.example.loanManage.service.BorrowerService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController // Marks this as a REST API controller that returns data (JSON).
@RequestMapping("/api/borrowers") // Base URL for all borrower-related tasks.
@CrossOrigin(origins = "*") // Allows external frontends (React/Vue) to access these APIs.
public class BorrowerController {

    private final BorrowerService borrowerService; // Reference to the service containing the business logic.

    public BorrowerController(BorrowerService borrowerService) { // Injects the service into the controller.
        this.borrowerService = borrowerService;
    }

    // ================= CREATE =================
    @PostMapping // Handles HTTP POST requests to create a new borrower.
    public ResponseEntity<?> create(@Valid @RequestBody BorrowerDto dto, BindingResult result) {
        // @Valid checks if the input (email, phone, etc.) follows the rules defined in BorrowerDto.
        // BindingResult holds any validation errors found.

        if (result.hasErrors()) { // If there are validation errors (e.g., empty name)...
            return ResponseEntity.badRequest().body( // Return a 400 Bad Request status.
                    result.getFieldErrors().stream()
                            .map(err -> err.getField() + ": " + err.getDefaultMessage()) // Format errors into a list.
                            .toList()
            );
        }
        return ResponseEntity.ok(borrowerService.create(dto)); // If valid, call service to save and return 200 OK.
    }

    // ================= GET ALL =================
    @GetMapping // Handles HTTP GET to fetch all borrowers.
    public List<BorrowerDto> getAll() {
        return borrowerService.getAll(); // Returns a simple list of all borrowers.
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}") // Handles GET for a specific ID (e.g., /api/borrowers/5).
    public BorrowerDto getById(@PathVariable Long id) { // @PathVariable picks the 'id' from the URL.
        return borrowerService.getById(id);
    }

    // ================= UPDATE =================
    @PutMapping("/{id}") // Handles HTTP PUT to update existing borrower info.
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody BorrowerDto dto, BindingResult result) {
        if (result.hasErrors()) { // Again, check for validation errors before updating.
            return ResponseEntity.badRequest().body(
                    result.getFieldErrors().stream()
                            .map(err -> err.getField() + ": " + err.getDefaultMessage())
                            .toList()
            );
        }
        return ResponseEntity.ok(borrowerService.update(id, dto)); // Send updated data to service.
    }

    // ================= PAGINATION =================
    @GetMapping("/page") // Handles paginated requests (e.g., /api/borrowers/page?page=0&size=8).
    public Page<BorrowerDto> getBorrowersPage(
            @RequestParam(defaultValue = "0") int page, // Which page to show.
            @RequestParam(defaultValue = "8") int size // How many items per page.
    ) {
        return borrowerService.getPage(page, size); // Returns a "Page" object with data and metadata (total pages, etc.).
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}") // Handles HTTP DELETE to remove a borrower.
    public void delete(@PathVariable Long id) {
        borrowerService.delete(id); // Calls service to delete the record from DB.
    }

    // =============== GLOBAL VALIDATION HANDLER ================
    @ResponseStatus(org.springframework.http.HttpStatus.BAD_REQUEST) // Tells Spring this method returns 400 status.
    @ExceptionHandler(MethodArgumentNotValidException.class) // Catches errors when @Valid fails.
    public Map<String, String> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>(); // Create a map to store field names and error messages.
        ex.getBindingResult().getAllErrors().forEach(err -> {
            String field = ((FieldError) err).getField(); // Identify which field failed (e.g., "email").
            String message = err.getDefaultMessage(); // Get the error reason (e.g., "invalid format").
            errors.put(field, message); // Add to the map.
        });
        return errors; // Return the map as JSON to the frontend.
    }
}