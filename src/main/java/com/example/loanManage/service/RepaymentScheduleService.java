package com.example.loanManage.service;

import com.example.loanManage.dto.RepaymentScheduleDto;
import java.util.List;

public interface RepaymentScheduleService {
    List<RepaymentScheduleDto> generateAllInstallments(String loanNumber);
    RepaymentScheduleDto payAndGenerateNext(String loanNumber);
    // View generated installments (history)
    List<RepaymentScheduleDto> getByLoanNumber(String loanNumber);
}
