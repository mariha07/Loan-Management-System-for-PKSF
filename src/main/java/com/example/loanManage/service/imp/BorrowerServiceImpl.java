package com.example.loanManage.service.imp;

import com.example.loanManage.dto.BorrowerDto;
import com.example.loanManage.entity.Address;
import com.example.loanManage.entity.Borrower;
import com.example.loanManage.repository.BorrowerRepository;
import com.example.loanManage.service.BorrowerService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@Service // Marks this as a Service bean so Spring can inject it into Controllers.
public class BorrowerServiceImpl implements BorrowerService {

    private final BorrowerRepository borrowerRepository; // Reference to the database tool.

    public BorrowerServiceImpl(BorrowerRepository borrowerRepository) { // Connects the repository via the constructor.
        this.borrowerRepository = borrowerRepository;
    }

    @Override
    public BorrowerDto create(BorrowerDto dto) { // Method to save a new borrower.
        Borrower borrower = new Borrower(); // Creates a fresh Entity object for the database.
        copyDtoToEntity(dto, borrower); // Transfers data from the incoming DTO into the Entity.
        Borrower saved = borrowerRepository.save(borrower); // Saves the entity to the DB.
        return toDto(saved); // Converts the saved entity back to a DTO to send to the UI.
    }

    @Override
    public List<BorrowerDto> getAll() { // Fetches all borrowers.
        return borrowerRepository.findAllByOrderByIdDesc() // Gets all records, newest first.
                .stream() // Starts a loop/process for each item.
                .map(this::toDto) // Converts every 'Borrower' entity into a 'BorrowerDto'.
                .collect(Collectors.toList()); // Collects them back into a List.
    }

    @Override
    public BorrowerDto getById(Long id) { // Finds one specific borrower.
        Borrower borrower = borrowerRepository.findById(id) // Searches by ID.
                .orElseThrow(() -> new RuntimeException("Borrower not found")); // Throws error if ID doesn't exist.
        return toDto(borrower);
    }

    @Override
    public BorrowerDto update(Long id, BorrowerDto dto) { // Updates existing info.
        Borrower borrower = borrowerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Borrower not found"));
        copyDtoToEntity(dto, borrower); // Overwrites old data with the new data from the DTO.
        Borrower updated = borrowerRepository.save(borrower); // Saves the changes.
        return toDto(updated);
    }

    @Override
    public void delete(Long id) { // Removes a borrower from the DB.
        Borrower borrower = borrowerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Borrower not found"));
        borrowerRepository.delete(borrower);
    }

    @Override
    @Transactional(readOnly = true) // Optimization: Tells DB this is a read-only task for better performance.
    public Page<BorrowerDto> getPage(int page, int size) { // Handles Pagination.
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending()); // Defines page number and sort order.
        return borrowerRepository.findAll(pageable) // Fetches only a specific chunk (page) of data.
                .map(this::toDto); // Converts that page of entities into a page of DTOs.
    }

    // ================= Helper Methods ==================

    private void copyDtoToEntity(BorrowerDto dto, Borrower b) { // Manually maps DTO fields to the Entity.
        b.setName(dto.getName());
        b.setIdType(dto.getIdType());
        // ... (sets simple fields) ...

        Address present = new Address(); // Creates the 'Embedded' address object.
        present.setDivision(dto.getPresentDivision());
        present.setDistrict(dto.getPresentDistrict());
        present.setUpazila(dto.getPresentUpazila());
        b.setPresentAddress(present); // Attaches the address to the borrower.

        // ... (repeats for permanent address) ...
    }

    private BorrowerDto toDto(Borrower b) { // Manually maps Entity data back to the DTO for the frontend.
        BorrowerDto dto = new BorrowerDto();
        dto.setId(b.getId());
        dto.setName(b.getName());
        // ... (extracts address fields back into the flat DTO structure) ...
        return dto;
    }

    private String convertToYYYYMMDD(String ddmmyyyy) { // Formats date strings so the DB can understand them.
        try {
            if (ddmmyyyy.contains("/")) {
                String[] p = ddmmyyyy.split("/");
                return p[2] + "-" + p[1] + "-" + p[0]; // Flips DD/MM/YYYY to YYYY-MM-DD.
            }
            return ddmmyyyy;
        } catch (Exception e) {
            return ddmmyyyy;
        }
    }
}

