package com.example.loanManage.service.imp;

import com.example.loanManage.dto.LoanProductDto;
import com.example.loanManage.entity.LoanProduct;
import com.example.loanManage.repository.LoanProductRepository;
import com.example.loanManage.service.LoanProductService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoanProductServiceImpl implements LoanProductService {

    private final LoanProductRepository loanProductRepository;

    public LoanProductServiceImpl(LoanProductRepository loanProductRepository) {
        this.loanProductRepository = loanProductRepository;
    }

    @Override
    public LoanProductDto create(LoanProductDto request) {

        validate(request); // 🔥 validate incoming data

        LoanProduct lp = new LoanProduct();
        copyDtoToEntity(request, lp);
        lp.setActive(true);

        LoanProduct saved = loanProductRepository.save(lp);
        return toDto(saved);
    }

    @Override
    public List<LoanProductDto> getAll() {
        return loanProductRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<LoanProductDto> getActive() {
        return loanProductRepository.findByActiveTrue()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public LoanProductDto getById(Long id) {
        LoanProduct lp = loanProductRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan product not found with id: " + id));
        return toDto(lp);
    }

    @Override
    public LoanProductDto update(Long id, LoanProductDto request) {

        validate(request); // 🔥 prevent null update crash

        LoanProduct lp = loanProductRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan product not found"));

        copyDtoToEntity(request, lp);

        LoanProduct saved = loanProductRepository.save(lp);
        return toDto(saved);
    }

    @Override
    public void deactivate(Long id) {
        LoanProduct lp = loanProductRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan product not found"));
        lp.setActive(false);
        loanProductRepository.save(lp);
    }

    // ======================== Helper Methods ========================

    private LoanProductDto toDto(LoanProduct lp) {
        LoanProductDto dto = new LoanProductDto();

        dto.setId(lp.getId());
        dto.setName(lp.getName());
        dto.setLoanType(lp.getLoanType());
        dto.setInterestRate(lp.getInterestRate());
        dto.setNumberOfInstallments(lp.getNumberOfInstallments());
        dto.setInstallmentType(lp.getInstallmentType());
        dto.setActive(lp.isActive());

        dto.setCode(lp.getCode());
        dto.setMaxLoan(lp.getMaxLoan());
        dto.setMinLoan(lp.getMinLoan());

        return dto;
    }

    private void copyDtoToEntity(LoanProductDto dto, LoanProduct lp) {
        lp.setName(dto.getName());
        lp.setLoanType(dto.getLoanType());
        lp.setInterestRate(dto.getInterestRate());
        lp.setNumberOfInstallments(dto.getNumberOfInstallments());
        lp.setInstallmentType(dto.getInstallmentType());

        lp.setCode(dto.getCode());
        lp.setMaxLoan(dto.getMaxLoan());
        lp.setMinLoan(dto.getMinLoan());
    }

    // 🔥 Validation to avoid null crash from frontend
    private void validate(LoanProductDto dto) {
        if (dto.getName() == null || dto.getName().isBlank())
            throw new RuntimeException("Loan Product Name is required");

        if (dto.getCode() == null || dto.getCode().isBlank())
            throw new RuntimeException("Loan Product Code is required");

        if (dto.getMaxLoan() == null)
            throw new RuntimeException("Max Loan amount is required");

        if (dto.getMinLoan() == null)
            throw new RuntimeException("Min Loan amount is required");

        if (dto.getInterestRate() == null)
            throw new RuntimeException("Interest rate is required");

        if (dto.getNumberOfInstallments() == null)
            throw new RuntimeException("Number of installments is required");

        if (dto.getInstallmentType() == null)
            throw new RuntimeException("Installment type is required");
    }
}
