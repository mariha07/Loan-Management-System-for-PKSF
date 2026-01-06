package com.example.loanManage.service.imp;

import com.example.loanManage.dto.RepaymentScheduleDto;
import com.example.loanManage.entity.LoanAccount;
import com.example.loanManage.entity.RepaymentSchedule;
import com.example.loanManage.repository.LoanAccountRepository;
import com.example.loanManage.repository.RepaymentScheduleRepository;
import com.example.loanManage.service.RepaymentScheduleService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class RepaymentScheduleServiceImpl implements RepaymentScheduleService {

    private final LoanAccountRepository loanAccountRepository;
    private final RepaymentScheduleRepository repository;

    public RepaymentScheduleServiceImpl(
            LoanAccountRepository loanAccountRepository,
            RepaymentScheduleRepository repository) {
        this.loanAccountRepository = loanAccountRepository;
        this.repository = repository;
    }

    // ================= GENERATE SCHEDULE =================
    @Override
    public List<RepaymentScheduleDto> generateSchedule(String loanNumber) {

        if (loanNumber == null || loanNumber.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Loan number is required"
            );
        }

        LoanAccount loan = loanAccountRepository
                .findByLoanNumber(loanNumber)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Loan account not found"
                        ));

        // 🔴 DUPLICATE CHECK
        if (repository.existsByLoanAccount_LoanNumber(loanNumber)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Repayment schedule already generated"
            );
        }

        // 🔴 NULL SAFETY CHECKS
        if (loan.getLoanProduct() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Loan product not linked"
            );
        }

        Integer n = loan.getLoanProduct().getNumberOfInstallments();
        if (n == null || n <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Number of installments not set in loan product"
            );
        }

        Double rate = loan.getLoanProduct().getInterestRate();
        if (rate == null || rate <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Interest rate not set in loan product"
            );
        }

        BigDecimal principal = loan.getApprovedAmount();
        if (principal == null || principal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Approved amount invalid"
            );
        }

        BigDecimal principalPerInstallment =
                principal.divide(
                        BigDecimal.valueOf(n),
                        2,
                        RoundingMode.HALF_UP
                );

        BigDecimal interestPerInstallment =
                principal.multiply(BigDecimal.valueOf(rate))
                        .divide(BigDecimal.valueOf(100 * n),
                                2,
                                RoundingMode.HALF_UP);

        List<RepaymentScheduleDto> result = new ArrayList<>();

        for (int i = 1; i <= n; i++) {

            RepaymentSchedule rs = new RepaymentSchedule();
            rs.setLoanAccount(loan);
            rs.setInstallmentNo(i);
            rs.setRepaymentDate(LocalDate.now().plusMonths(i));
            rs.setPrincipalOutstanding(principalPerInstallment);
            rs.setInterestOutstanding(interestPerInstallment);

            repository.save(rs);

            RepaymentScheduleDto dto = new RepaymentScheduleDto();
            dto.setId(rs.getId());
            dto.setLoanNumber(loanNumber);
            dto.setInstallmentNo(i);
            dto.setRepaymentDate(rs.getRepaymentDate().toString());
            dto.setPrincipalOutstanding(rs.getPrincipalOutstanding());
            dto.setInterestOutstanding(rs.getInterestOutstanding());

            result.add(dto);
        }

        return result;
    }



    // ================= GET BY LOAN NUMBER =================
    @Override
    public List<RepaymentScheduleDto> getByLoanNumber(String loanNumber) {

        List<RepaymentSchedule> list =
                repository.findByLoanAccount_LoanNumberOrderByInstallmentNoAsc(loanNumber);

        List<RepaymentScheduleDto> res = new ArrayList<>();

        for (RepaymentSchedule rs : list) {
            RepaymentScheduleDto dto = new RepaymentScheduleDto();
            dto.setId(rs.getId());
            dto.setLoanNumber(loanNumber);
            dto.setInstallmentNo(rs.getInstallmentNo());
            dto.setRepaymentDate(rs.getRepaymentDate().toString());
            dto.setPrincipalOutstanding(rs.getPrincipalOutstanding());
            dto.setInterestOutstanding(rs.getInterestOutstanding());
         //   dto.setStatus(rs.getStatus());
            res.add(dto);
        }
        return res;
    }
}
