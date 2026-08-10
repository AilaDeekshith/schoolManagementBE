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
    List<Fees> searchFees(String className, String academicYear, Fees.FeeStatus status, String name);
    List<String> getAcademicYears();
    BigDecimal getTotalCollected();
    BigDecimal getTotalOutstanding();

    /** Emails an outstanding-fee reminder for a single fee record. */
    void sendFeeReminder(Long feeId);

    /**
     * Emails reminders for every outstanding fee (due amount &gt; 0), optionally
     * scoped to an academic year. Returns the number of emails dispatched.
     */
    int sendReminders(String academicYear);
}