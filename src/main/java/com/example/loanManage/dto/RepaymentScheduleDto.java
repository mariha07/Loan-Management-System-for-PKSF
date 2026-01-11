package com.example.loanManage.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RepaymentScheduleDto {

    private Long id;
    private String loanNumber;
    private Integer installmentNo;
    private String repaymentDate;

    // কিস্তির হিসাব
    private BigDecimal principalPaid;     // আসলের অংশ
    private BigDecimal interestPaid;      // সুদের অংশ
    private BigDecimal installmentAmount; // মোট কিস্তির পরিমাণ (Principal + Interest)

    // পেমেন্ট কালেকশন ট্র্যাকিং (এটি যোগ করা হয়েছে এরর ফিক্স করতে)
    private BigDecimal paidAmount;        // গ্রাহক বর্তমানে কত টাকা জমা দিয়েছে

    // বকেয়া হিসাব
    private BigDecimal principalOutstanding;
    private BigDecimal interestOutstanding;

    private String status; // PENDING, PAID, PARTIAL
}