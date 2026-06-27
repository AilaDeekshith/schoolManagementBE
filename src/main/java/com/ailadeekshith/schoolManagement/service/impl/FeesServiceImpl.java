package com.ailadeekshith.schoolManagement.service.impl;

import com.ailadeekshith.schoolManagement.exception.ResourceNotFoundException;
import com.ailadeekshith.schoolManagement.model.Fees;
import com.ailadeekshith.schoolManagement.model.Student;
import com.ailadeekshith.schoolManagement.repository.FeesRepository;
import com.ailadeekshith.schoolManagement.repository.StudentRepository;
import com.ailadeekshith.schoolManagement.service.FeesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FeesServiceImpl implements FeesService {

    private final FeesRepository feesRepository;
    private final StudentRepository studentRepository;

    @Override
    public Fees createFeeRecord(Fees fees) {
        log.info("Creating fee record for student id: {}", fees.getStudent().getId());
        // Ensure student exists
        Student student = studentRepository.findById(fees.getStudent().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        fees.setStudent(student);
        fees.setDueAmount(fees.getTotalAmount().subtract(fees.getPaidAmount()));
        return feesRepository.save(fees);
    }

    @Override
    @Transactional(readOnly = true)
    public Fees getFeeById(Long id) {
        return feesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fee record not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Fees> getAllFees() {
        return feesRepository.findAll();
    }

    @Override
    public Fees updateFee(Long id, Fees updated) {
        Fees existing = getFeeById(id);
        existing.setTotalAmount(updated.getTotalAmount());
        existing.setAcademicYear(updated.getAcademicYear());
        existing.setFeeType(updated.getFeeType());
        existing.setDueDate(updated.getDueDate());
        existing.setDueAmount(existing.getTotalAmount().subtract(existing.getPaidAmount()));
        log.info("Updated fee record id: {}", id);
        return feesRepository.save(existing);
    }

    @Override
    public void deleteFee(Long id) {
        Fees fees = getFeeById(id);
        feesRepository.delete(fees);
        log.info("Deleted fee record id: {}", id);
    }

    @Override
    public Fees collectPayment(Long feeId, BigDecimal amount, Fees.PaymentMethod method, String transactionId) {
        Fees fees = getFeeById(feeId);

        if (amount.compareTo(fees.getDueAmount()) > 0) {
            throw new IllegalArgumentException("Payment amount exceeds due amount");
        }

        BigDecimal newPaid = fees.getPaidAmount().add(amount);
        BigDecimal newDue  = fees.getTotalAmount().subtract(newPaid);

        fees.setPaidAmount(newPaid);
        fees.setDueAmount(newDue);
        fees.setPaymentMethod(method);
        fees.setTransactionId(transactionId);
        fees.setPaymentDate(LocalDate.now());
        fees.setFeeStatus(newDue.compareTo(BigDecimal.ZERO) <= 0 ? Fees.FeeStatus.PAID : Fees.FeeStatus.PENDING);

        // also update student fee status
        Student student = fees.getStudent();
        student.setFeeStatus(newDue.compareTo(BigDecimal.ZERO) <= 0
                ? Student.FeeStatus.PAID : Student.FeeStatus.PENDING);
        studentRepository.save(student);

        log.info("Collected ₹{} for fee id: {}", amount, feeId);
        return feesRepository.save(fees);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Fees> getFeesByStudent(Long studentId) {
        return feesRepository.findByStudentId(studentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Fees> getFeesByStatus(Fees.FeeStatus status) {
        return feesRepository.findByFeeStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Fees> getFeesByAcademicYear(String academicYear) {
        return feesRepository.findByAcademicYear(academicYear);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalCollected() {
        BigDecimal total = feesRepository.getTotalCollected();
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalOutstanding() {
        BigDecimal total = feesRepository.getTotalOutstanding();
        return total != null ? total : BigDecimal.ZERO;
    }
}