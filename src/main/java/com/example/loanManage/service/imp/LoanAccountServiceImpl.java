package com.example.loanManage.service.imp;

import com.example.loanManage.dto.LoanAccountDto;
import com.example.loanManage.entity.Borrower;
import com.example.loanManage.entity.LoanAccount;
import com.example.loanManage.entity.LoanProduct;
import com.example.loanManage.repository.BorrowerRepository;
import com.example.loanManage.repository.LoanAccountRepository;
import com.example.loanManage.repository.LoanProductRepository;
import com.example.loanManage.service.LoanAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
@Service
@Transactional
public class LoanAccountServiceImpl implements LoanAccountService {

    private final LoanAccountRepository loanAccountRepository;
    private final BorrowerRepository borrowerRepository;
    private final LoanProductRepository loanProductRepository;

    public LoanAccountServiceImpl(LoanAccountRepository loanAccountRepository,
                                  BorrowerRepository borrowerRepository,
                                  LoanProductRepository loanProductRepository) {
        this.loanAccountRepository = loanAccountRepository;
        this.borrowerRepository = borrowerRepository;
        this.loanProductRepository = loanProductRepository;
    }

    // ===========================
    // CREATE LOAN ACCOUNT
    // ===========================
    @Override
    public LoanAccountDto create(LoanAccountDto dto) {

        if (dto.getBorrowerId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Borrower is required");
        if (dto.getLoanProductId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Loan product is required");
        if (dto.getApprovedAmount() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Approved amount is required");

        Borrower borrower = borrowerRepository.findById(dto.getBorrowerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Borrower not found"));

        LoanProduct product = loanProductRepository.findById(dto.getLoanProductId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Loan product not found"));

        // ===== MIN–MAX VALIDATION =====
        BigDecimal approved = dto.getApprovedAmount();

        if (product.getMinLoan() != null &&
                approved.compareTo(BigDecimal.valueOf(product.getMinLoan())) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Approved amount must be ≥ " + product.getMinLoan());
        }

        if (product.getMaxLoan() != null &&
                approved.compareTo(BigDecimal.valueOf(product.getMaxLoan())) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Approved amount must be ≤ " + product.getMaxLoan());
        }

        // ===== CREATE ENTITY =====
        LoanAccount acc = new LoanAccount();
        acc.setBorrower(borrower);
        acc.setLoanProduct(product);
        acc.setApprovedAmount(dto.getApprovedAmount());

        LocalDate date = dto.getOpeningDate() != null
                ? LocalDate.parse(dto.getOpeningDate())
                : LocalDate.now();
        acc.setOpeningDate(date);

        acc.setStatus(dto.getStatus() != null ? dto.getStatus() : "OPEN");

        // ===== AUTO-GENERATE LOAN NUMBER =====
        String nextLoanNumber = getNextLoanNumber();
        acc.setLoanNumber(nextLoanNumber);

        LoanAccount saved = loanAccountRepository.save(acc);

        return toDto(saved);
    }

    // ===========================
    // NEXT LOAN NUMBER (6 DIGITS)
    // ===========================
    @Override
    @Transactional(readOnly = true)
    public String getNextLoanNumber() {

        Optional<LoanAccount> last = loanAccountRepository.findTopByOrderByIdDesc();

        long nextId = last.map(a -> a.getId() + 1).orElse(1L);

        return String.format("LN-%06d", nextId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LoanAccountDto> getPaged(int page, int size) {
        return loanAccountRepository
                .findAll(PageRequest.of(page, size))
                .map(this::toDto);
    }

    // ===========================
    // GET BY ID
    // ===========================
    @Override
    @Transactional(readOnly = true)
    public LoanAccountDto getById(Long id) {
        LoanAccount acc = loanAccountRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Loan account not found"));
        return toDto(acc);
    }

    // ===========================
    // ENTITY → DTO
    // ===========================
    private LoanAccountDto toDto(LoanAccount acc) {

        LoanAccountDto dto = new LoanAccountDto();

        dto.setId(acc.getId());
        dto.setLoanNumber(acc.getLoanNumber());

        // Borrower
        if (acc.getBorrower() != null) {
            Borrower b = acc.getBorrower();
            dto.setBorrowerId(b.getId());
            dto.setBorrowerName(b.getName());
            dto.setBorrowerMobile(b.getMobile());
            if (b.getIdType() != null)
                dto.setBorrowerIdType(b.getIdType().name());
            dto.setBorrowerIdNumber(b.getIdNumber());
        }

        // Product
        if (acc.getLoanProduct() != null) {
            LoanProduct lp = acc.getLoanProduct();

            dto.setLoanProductId(lp.getId());
            dto.setLoanProductName(lp.getName());

            if (lp.getInterestRate() != null)
                dto.setInterestRate(BigDecimal.valueOf(lp.getInterestRate()));

            if (lp.getInstallmentType() != null)
                dto.setInstallmentType(lp.getInstallmentType().name());

            dto.setNumberOfInstallments(lp.getNumberOfInstallments());
        }

        // Account values
        dto.setApprovedAmount(acc.getApprovedAmount());
        dto.setOpeningDate(acc.getOpeningDate() != null ? acc.getOpeningDate().toString() : null);
        dto.setStatus(acc.getStatus());

        return dto;
    }


}
