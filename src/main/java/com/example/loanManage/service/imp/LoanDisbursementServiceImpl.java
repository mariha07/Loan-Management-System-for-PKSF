package com.example.loanManage.service.imp;

import com.example.loanManage.dto.LoanDisbursementDto;
import com.example.loanManage.entity.LoanAccount;
import com.example.loanManage.entity.LoanDisbursement;
import com.example.loanManage.repository.LoanAccountRepository;
import com.example.loanManage.repository.LoanDisbursementRepository;
import com.example.loanManage.service.LoanDisbursementService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LoanDisbursementServiceImpl implements LoanDisbursementService {

    private final LoanAccountRepository loanAccountRepository;
    private final LoanDisbursementRepository disbursementRepository;

    public LoanDisbursementServiceImpl(
            LoanAccountRepository loanAccountRepository,
            LoanDisbursementRepository disbursementRepository) {
        this.loanAccountRepository = loanAccountRepository;
        this.disbursementRepository = disbursementRepository;
    }

    // ================= DISBURSE =================
    @Override
    public LoanDisbursementDto disburse(LoanDisbursementDto dto) {

        if (dto.getLoanAccountId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Loan account id required");
        }

        if (dto.getDisbursementAmount() == null ||
                dto.getDisbursementAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Invalid disbursement amount");
        }

        LoanAccount loan = loanAccountRepository.findById(dto.getLoanAccountId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "Loan account not found"));

        if (dto.getDisbursementAmount()
                .compareTo(loan.getApprovedAmount()) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Disbursement exceeds approved amount");
        }

        LoanDisbursement disbursement = new LoanDisbursement();
        disbursement.setLoanAccount(loan);
        disbursement.setDisbursementAmount(dto.getDisbursementAmount());


        LoanDisbursement saved = disbursementRepository.save(disbursement);

        return mapToDto(saved);
    }

    // ================= FIND BY LOAN NUMBER =================
    @Override
    public LoanDisbursementDto findByLoanNumber(String loanNumber) {

        LoanAccount loan = loanAccountRepository
                .findByLoanNumber(loanNumber)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Loan account not found"));

        LoanDisbursement d = disbursementRepository
                .findTopByLoanAccountOrderByIdDesc(loan)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "No disbursement found"));

        return mapToDto(d);
    }

    // ================= GET ALL =================
    @Override
    public List<LoanDisbursementDto> getAll() {

        return disbursementRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // ================= MAPPER =================
    private LoanDisbursementDto mapToDto(LoanDisbursement d) {

        LoanDisbursementDto dto = new LoanDisbursementDto();
        dto.setId(d.getId());
        dto.setLoanAccountId(d.getLoanAccount().getId());
        dto.setLoanNumber(d.getLoanAccount().getLoanNumber());
        dto.setBorrowerName(d.getLoanAccount().getBorrower().getName());
        dto.setLoanProductName(d.getLoanAccount().getLoanProduct().getName());
        dto.setApprovedAmount(d.getLoanAccount().getApprovedAmount());
        dto.setDisbursementAmount(d.getDisbursementAmount());

        return dto;
    }
}
