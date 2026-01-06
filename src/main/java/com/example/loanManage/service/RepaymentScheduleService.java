package com.example.loanManage.service;

import com.example.loanManage.dto.RepaymentScheduleDto;
import java.util.List;

public interface RepaymentScheduleService {

    List<RepaymentScheduleDto> generateSchedule(String loanNumber);

    List<RepaymentScheduleDto> getByLoanNumber(String loanNumber);
}
