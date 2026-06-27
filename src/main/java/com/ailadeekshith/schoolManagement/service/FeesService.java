package com.ailadeekshith.schoolManagement.service;

import com.ailadeekshith.schoolManagement.model.Fees;

import java.math.BigDecimal;
import java.util.List;

public interface FeesService {
    Fees createFeeRecord(Fees fees);
    Fees getFeeById(Long id);
    List<Fees> getAllFees();
    Fees updateFee(Long id, Fees fees);
    void deleteFee(Long id);

    Fees collectPayment(Long feeId, BigDecimal amount, Fees.PaymentMethod method, String transactionId);
    List<Fees> getFeesByStudent(Long studentId);
    List<Fees> getFeesByStatus(Fees.FeeStatus status);
    List<Fees> getFeesByAcademicYear(String academicYear);
    BigDecimal getTotalCollected();
    BigDecimal getTotalOutstanding();
}