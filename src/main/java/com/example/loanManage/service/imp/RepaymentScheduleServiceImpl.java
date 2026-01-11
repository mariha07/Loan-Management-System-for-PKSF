package com.example.loanManage.service.imp;

import com.example.loanManage.dto.RepaymentScheduleDto;
import com.example.loanManage.entity.InstallmentType;
import com.example.loanManage.entity.LoanAccount;
import com.example.loanManage.entity.RepaymentSchedule;
import com.example.loanManage.repository.LoanAccountRepository;
import com.example.loanManage.repository.RepaymentScheduleRepository;
import com.example.loanManage.service.RepaymentScheduleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class RepaymentScheduleServiceImpl implements RepaymentScheduleService {

    private final LoanAccountRepository loanAccountRepository;
    private final RepaymentScheduleRepository repository;

    @Override
    public List<RepaymentScheduleDto> generateAllInstallments(String loanNumber) {
        LoanAccount loan = loanAccountRepository
                .findByLoanNumber(loanNumber)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Loan account not found"));

        if (repository.existsByLoanAccount_LoanNumber(loanNumber)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Repayment schedule already exists");
        }

        BigDecimal principal = loan.getApprovedAmount();
        int n = loan.getLoanProduct().getNumberOfInstallments();
        double rate = loan.getLoanProduct().getInterestRate();
        InstallmentType type = loan.getLoanProduct().getInstallmentType();

        BigDecimal principalPerInstallment = principal.divide(BigDecimal.valueOf(n), 2, RoundingMode.HALF_UP);

        BigDecimal yearDivisor = (type == InstallmentType.WEEKLY) ? BigDecimal.valueOf(52) : BigDecimal.valueOf(12);
        BigDecimal timeInYears = BigDecimal.valueOf(n).divide(yearDivisor, 10, RoundingMode.HALF_UP);
        BigDecimal totalInterest = principal.multiply(BigDecimal.valueOf(rate))
                .multiply(timeInYears)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal interestPerInstallment = totalInterest.divide(BigDecimal.valueOf(n), 2, RoundingMode.HALF_UP);

        BigDecimal installmentAmount = principalPerInstallment.add(interestPerInstallment);
        LocalDate baseDate = loan.getDisbursementDate() != null ? loan.getDisbursementDate() : loan.getOpeningDate();

        for (int i = 1; i <= n; i++) {
            RepaymentSchedule rs = new RepaymentSchedule();
            rs.setLoanAccount(loan);
            rs.setInstallmentNo(i);
            rs.setRepaymentDate(type == InstallmentType.WEEKLY ? baseDate.plusWeeks(i) : baseDate.plusMonths(i));
            rs.setPrincipalPaid(principalPerInstallment);
            rs.setInterestPaid(interestPerInstallment);
            rs.setInstallmentAmount(installmentAmount);

            BigDecimal principalOutstanding = principal.subtract(principalPerInstallment.multiply(BigDecimal.valueOf(i)));
            BigDecimal interestOutstanding = totalInterest.subtract(interestPerInstallment.multiply(BigDecimal.valueOf(i)));

            if (i == n) {
                rs.setPrincipalOutstanding(BigDecimal.ZERO);
                rs.setInterestOutstanding(BigDecimal.ZERO);
            } else {
                rs.setPrincipalOutstanding(principalOutstanding);
                rs.setInterestOutstanding(interestOutstanding);
            }

            rs.setStatus("PENDING");
            repository.save(rs);
        }

        return getByLoanNumber(loanNumber);
    }

    @Override
    public RepaymentScheduleDto payAndGenerateNext(String loanNumber) {

        LoanAccount loan = loanAccountRepository
                .findByLoanNumber(loanNumber)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Loan account not found"));

        // Find the first PENDING installment
        RepaymentSchedule current =
                repository.findByLoanAccount_LoanNumberOrderByInstallmentNoAsc(loanNumber)
                        .stream()
                        .filter(rs -> "PENDING".equals(rs.getStatus()))
                        .findFirst()
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.BAD_REQUEST, "No pending installments found"));

        // mark PAID
        current.setStatus("PAID");
        repository.save(current);

        return toDto(current);
    }

    @Override
    public List<RepaymentScheduleDto> getByLoanNumber(String loanNumber) {
        return repository.findByLoanAccount_LoanNumberOrderByInstallmentNoAsc(loanNumber)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private RepaymentScheduleDto toDto(RepaymentSchedule rs) {
        RepaymentScheduleDto dto = new RepaymentScheduleDto();
        dto.setLoanNumber(rs.getLoanAccount().getLoanNumber());
        dto.setInstallmentNo(rs.getInstallmentNo());
        dto.setRepaymentDate(rs.getRepaymentDate().toString());
        dto.setPrincipalPaid(rs.getPrincipalPaid());
        dto.setInterestPaid(rs.getInterestPaid());
        dto.setInstallmentAmount(rs.getInstallmentAmount());
        dto.setPrincipalOutstanding(rs.getPrincipalOutstanding());
        dto.setInterestOutstanding(rs.getInterestOutstanding());
        dto.setStatus(rs.getStatus());
        return dto;
    }
}
