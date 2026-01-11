package com.example.loanManage.service;

import com.example.loanManage.dto.RepaymentCollectionRequest;
import com.example.loanManage.dto.RepaymentScheduleDto;
import java.util.List;

public interface RepaymentScheduleService {
    List<RepaymentScheduleDto> getSchedulesByLoanNumber(String loanNumber);
    void collectPayment(RepaymentCollectionRequest request);
    List<RepaymentScheduleDto> generateAllInstallments(String loanNumber);
    RepaymentScheduleDto payAndGenerateNext(String loanNumber);
    List<RepaymentScheduleDto> getByLoanNumber(String loanNumber);
}