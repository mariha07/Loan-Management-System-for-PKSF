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

@Service

public class BorrowerServiceImpl implements BorrowerService {

    private final BorrowerRepository borrowerRepository;

    public BorrowerServiceImpl(BorrowerRepository borrowerRepository) {
        this.borrowerRepository = borrowerRepository;
    }

    @Override
    public BorrowerDto create(BorrowerDto dto) {
        Borrower borrower = new Borrower();
        copyDtoToEntity(dto, borrower);

        Borrower saved = borrowerRepository.save(borrower);
        return toDto(saved);
    }

    @Override
    public List<BorrowerDto> getAll() {
        return //borrowerRepository.findAll()
                borrowerRepository.findAllByOrderByIdDesc()
                        .stream()
                        .map(this::toDto)
                        .collect(Collectors.toList());
    }

    @Override
    public BorrowerDto getById(Long id) {
        Borrower borrower = borrowerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Borrower not found"));
        return toDto(borrower);
    }

    @Override
    public BorrowerDto update(Long id, BorrowerDto dto) {
        Borrower borrower = borrowerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Borrower not found"));

        copyDtoToEntity(dto, borrower);
        Borrower updated = borrowerRepository.save(borrower);

        return toDto(updated);
    }

    @Override
    public void delete(Long id) {
        Borrower borrower = borrowerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Borrower not found"));
        borrowerRepository.delete(borrower);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BorrowerDto> getPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        return borrowerRepository.findAll(pageable)
                .map(this::toDto);
    }


    // ================= Helper Methods ==================

    private void copyDtoToEntity(BorrowerDto dto, Borrower b) {

        b.setName(dto.getName());
        b.setIdType(dto.getIdType());
        b.setIdNumber(dto.getIdNumber());
        b.setDateOfBirth(dto.getDateOfBirth());
        b.setGender(dto.getGender());
        b.setMobile(dto.getMobile());
        b.setEmail(dto.getEmail());

        Address present = new Address();
        present.setDivision(dto.getPresentDivision());
        present.setDistrict(dto.getPresentDistrict());
        present.setUpazila(dto.getPresentUpazila());
        b.setPresentAddress(present);

        Address permanent = new Address();
        permanent.setDivision(dto.getPermanentDivision());
        permanent.setDistrict(dto.getPermanentDistrict());
        permanent.setUpazila(dto.getPermanentUpazila());
        b.setPermanentAddress(permanent);
    }

    private BorrowerDto toDto(Borrower b) {
        BorrowerDto dto = new BorrowerDto();

        dto.setId(b.getId());
        dto.setName(b.getName());
        dto.setIdType(b.getIdType());
        dto.setIdNumber(b.getIdNumber());
        b.setDateOfBirth(convertToYYYYMMDD(dto.getDateOfBirth()));
        dto.setGender(b.getGender());
        dto.setMobile(b.getMobile());
        dto.setEmail(b.getEmail());

        if (b.getPresentAddress() != null) {
            dto.setPresentDivision(b.getPresentAddress().getDivision());
            dto.setPresentDistrict(b.getPresentAddress().getDistrict());
            dto.setPresentUpazila(b.getPresentAddress().getUpazila());
        }

        if (b.getPermanentAddress() != null) {
            dto.setPermanentDivision(b.getPermanentAddress().getDivision());
            dto.setPermanentDistrict(b.getPermanentAddress().getDistrict());
            dto.setPermanentUpazila(b.getPermanentAddress().getUpazila());
        }

        return dto;
    }

    private String convertToYYYYMMDD(String ddmmyyyy) {
        try {
            if (ddmmyyyy.contains("/")) {
                String[] p = ddmmyyyy.split("/");
                return p[2] + "-" + p[1] + "-" + p[0];
            }
            return ddmmyyyy;
        } catch (Exception e) {
            return ddmmyyyy;
        }
    }

}

