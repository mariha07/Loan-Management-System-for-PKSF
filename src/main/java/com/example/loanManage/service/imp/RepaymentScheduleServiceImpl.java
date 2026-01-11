package com.example.loanManage.service.imp;

import com.example.loanManage.dto.RepaymentCollectionRequest;
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
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class RepaymentScheduleServiceImpl implements RepaymentScheduleService {

    private final LoanAccountRepository loanAccountRepository;
    private final RepaymentScheduleRepository repository;

    @Override
    public List<RepaymentScheduleDto> getSchedulesByLoanNumber(String loanNumber) {
        return repository.findByLoanAccount_LoanNumberOrderByInstallmentNoAsc(loanNumber)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void collectPayment(RepaymentCollectionRequest request) {
        // লোন নাম্বার এবং কিস্তি নাম্বার দিয়ে নির্দিষ্ট কিস্তি খুঁজে বের করা
        RepaymentSchedule schedule =
                    repository.findByLoanAccount_LoanNumberAndInstallmentNo(
                            request.getLoanNumber(),
                            request.getInstallmentNo()
                    ).orElseThrow(() ->
                            new ResponseStatusException(
                                    HttpStatus.NOT_FOUND, "Installment not found"));

        if ("PAID".equals(schedule.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Installment already paid");
        }

        BigDecimal payment = request.getPaymentAmount();
        if (payment == null || payment.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid payment amount");
        }

        // পেমেন্ট আপডেট করা
        BigDecimal currentPaid = schedule.getPaidAmount() != null ? schedule.getPaidAmount() : BigDecimal.ZERO;
        BigDecimal totalPaidNow = currentPaid.add(payment);
        
        // Prevent overpayment beyond installment amount
        if (totalPaidNow.compareTo(schedule.getInstallmentAmount()) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment exceeds installment amount");
        }
        
        schedule.setPaidAmount(totalPaidNow);

        // বকেয়া (Outstanding) কমানো
        // Principal and Interest components are fixed per installment in this model
        // We calculate how much of the CURRENT payment goes to principal vs interest
        BigDecimal remainingForInterest = payment;
        
        // Logical approach: pay interest first, then principal (or as per business rule)
        // Here we follow the logic of reducing from the fixed interest/principal parts of the installment
        BigDecimal interestPart = schedule.getInterestPaid();
        BigDecimal principalPart = schedule.getPrincipalPaid();

        // If your business logic says Outstanding is the REMAINING balance for this installment:
        BigDecimal newPrincipalOut = principalPart.add(interestPart).subtract(totalPaidNow);
        // However, looking at your generateAllInstallments, Outstanding seems to be the Total Loan Balance.
        // If it is Total Loan Balance, we subtract the payment from it.
        
        if (schedule.getPrincipalOutstanding() != null) {
            schedule.setPrincipalOutstanding(schedule.getPrincipalOutstanding().subtract(payment));
        }

        // স্ট্যাটাস চেক করা (পুরো টাকা দিলে PAID, কম দিলে PARTIAL)
        if (totalPaidNow.compareTo(schedule.getInstallmentAmount()) >= 0) {
            schedule.setStatus("PAID");
            // Ensure outstanding for this specific installment hits zero if that's the logic
        } else {
            schedule.setStatus("PARTIAL");
        }

        repository.save(schedule);
    }

    @Override
    public List<RepaymentScheduleDto> generateAllInstallments(String loanNumber) {
        LoanAccount loan = loanAccountRepository
                .findByLoanNumber(loanNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Loan account not found"));

        // ডুপ্লিকেট চেক
        if (repository.existsByLoanAccount_LoanNumber(loanNumber)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Repayment schedule already exists");
        }

        BigDecimal principal = loan.getApprovedAmount();
        int n = loan.getLoanProduct().getNumberOfInstallments();
        double rate = loan.getLoanProduct().getInterestRate();
        InstallmentType type = loan.getLoanProduct().getInstallmentType();

        // কিস্তি ক্যালকুলেশন
        BigDecimal principalPerInstallment = principal.divide(BigDecimal.valueOf(n), 2, RoundingMode.HALF_UP);
        BigDecimal yearDivisor = (type == InstallmentType.WEEKLY) ? BigDecimal.valueOf(52) : BigDecimal.valueOf(12);
        BigDecimal timeInYears = BigDecimal.valueOf(n).divide(yearDivisor, 10, RoundingMode.HALF_UP);

        BigDecimal totalInterest = principal.multiply(BigDecimal.valueOf(rate))
                .multiply(timeInYears)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal interestPerInstallment = totalInterest.divide(BigDecimal.valueOf(n), 2, RoundingMode.HALF_UP);
        BigDecimal installmentAmount = principalPerInstallment.add(interestPerInstallment);

        LocalDate baseDate = loan.getDisbursementDate() != null ? loan.getDisbursementDate() : loan.getOpeningDate();

        // লুপ চালিয়ে সব কিস্তি তৈরি
        for (int i = 1; i <= n; i++) {
            RepaymentSchedule rs = new RepaymentSchedule();
            rs.setLoanAccount(loan);
            rs.setInstallmentNo(i);
            rs.setRepaymentDate(type == InstallmentType.WEEKLY ? baseDate.plusWeeks(i) : baseDate.plusMonths(i));
            rs.setPrincipalPaid(principalPerInstallment);
            rs.setInterestPaid(interestPerInstallment);
            rs.setInstallmentAmount(installmentAmount);
            rs.setPaidAmount(BigDecimal.ZERO);

            // বকেয়া (Outstanding) হিসাব
            if (i == n) {
                rs.setPrincipalOutstanding(BigDecimal.ZERO);
                rs.setInterestOutstanding(BigDecimal.ZERO);
            } else {
                rs.setPrincipalOutstanding(principal.subtract(principalPerInstallment.multiply(BigDecimal.valueOf(i))));
                rs.setInterestOutstanding(totalInterest.subtract(interestPerInstallment.multiply(BigDecimal.valueOf(i))));
            }

            rs.setStatus("PENDING");
            repository.save(rs);
        }

        return getSchedulesByLoanNumber(loanNumber);
    }

    @Override
    public RepaymentScheduleDto payAndGenerateNext(String loanNumber) {
        // এটি মূলত আপনার "Single-step" পেমেন্টের জন্য
        RepaymentSchedule current = repository.findByLoanAccount_LoanNumberOrderByInstallmentNoAsc(loanNumber)
                .stream()
                .filter(rs -> "PENDING".equals(rs.getStatus()) || "PARTIAL".equals(rs.getStatus()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No pending installments found"));

        current.setStatus("PAID");
        current.setPaidAmount(current.getInstallmentAmount()); // Full payment assumption
        repository.save(current);

        return toDto(current);
    }

    @Override
    public List<RepaymentScheduleDto> getByLoanNumber(String loanNumber) {
        return getSchedulesByLoanNumber(loanNumber);
    }

    private RepaymentScheduleDto toDto(RepaymentSchedule rs) {
        RepaymentScheduleDto dto = new RepaymentScheduleDto();
        dto.setLoanNumber(rs.getLoanAccount().getLoanNumber());
        dto.setInstallmentNo(rs.getInstallmentNo());
        dto.setRepaymentDate(rs.getRepaymentDate().toString());
        dto.setPrincipalPaid(rs.getPrincipalPaid());
        dto.setInterestPaid(rs.getInterestPaid());
        dto.setInstallmentAmount(rs.getInstallmentAmount());
        dto.setPaidAmount(rs.getPaidAmount());
        dto.setPrincipalOutstanding(rs.getPrincipalOutstanding());
        dto.setInterestOutstanding(rs.getInterestOutstanding());
        dto.setStatus(rs.getStatus());
        return dto;
    }
}