package com.example.loanManage.controller;

import com.example.loanManage.dto.BorrowerDto;
import com.example.loanManage.entity.Borrower;
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

@RestController
@RequestMapping("/api/borrowers")
@CrossOrigin(origins = "*")
public class BorrowerController {

    private final BorrowerService borrowerService;
    public BorrowerController(BorrowerService borrowerService) {
        this.borrowerService = borrowerService;
    }


    // ================= CREATE =================
    @PostMapping
    public ResponseEntity<?> create(
            @Valid @RequestBody BorrowerDto dto,
            BindingResult result
    ) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(
                    result.getFieldErrors().stream()
                            .map(err -> err.getField() + ": " + err.getDefaultMessage())
                            .toList()
            );
        }

        return ResponseEntity.ok(borrowerService.create(dto));
    }

    // ================= GET ALL =================
    @GetMapping
    public List<BorrowerDto> getAll() {
        return borrowerService.getAll();
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public BorrowerDto getById(@PathVariable Long id) {
        return borrowerService.getById(id);
    }

    // ================= UPDATE =================
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @Valid @RequestBody BorrowerDto dto,
            BindingResult result
    ) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(
                    result.getFieldErrors().stream()
                            .map(err -> err.getField() + ": " + err.getDefaultMessage())
                            .toList()
            );
        }

        return ResponseEntity.ok(borrowerService.update(id, dto));
    }

    @GetMapping("/page")
    public Page<BorrowerDto> getBorrowersPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size
    ) {
        return borrowerService.getPage(page, size);
    }



    // ================= DELETE =================
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        borrowerService.delete(id);
    }

    // =============== GLOBAL VALIDATION HANDLER ================
    @ResponseStatus(org.springframework.http.HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationErrors(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(err -> {
            String field = ((FieldError) err).getField();
            String message = err.getDefaultMessage();
            errors.put(field, message);
        });

        return errors;
    }


}
