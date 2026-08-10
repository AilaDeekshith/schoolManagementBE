package com.ailadeekshith.schoolManagement.service.impl;

import com.ailadeekshith.schoolManagement.exception.ResourceNotFoundException;
import com.ailadeekshith.schoolManagement.model.Fees;
import com.ailadeekshith.schoolManagement.model.Student;
import com.ailadeekshith.schoolManagement.repository.FeesRepository;
import com.ailadeekshith.schoolManagement.repository.StudentRepository;
import com.ailadeekshith.schoolManagement.service.EmailService;
import com.ailadeekshith.schoolManagement.service.EmailTemplates;
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
    private final EmailService emailService;

    private static final java.text.DecimalFormat AMOUNT_FMT = new java.text.DecimalFormat("#,##0.00");

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
    public List<Fees> searchFees(String className, String academicYear, Fees.FeeStatus status, String name) {
        // name uses "" (not null) as the "no filter" sentinel to avoid Postgres
        // typing a null parameter as bytea inside lower()/concat().
        return feesRepository.search(blankToNull(className), blankToNull(academicYear), status,
                name == null ? "" : name.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAcademicYears() {
        return feesRepository.findDistinctAcademicYears();
    }

    private String blankToNull(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
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

    @Override
    @Transactional(readOnly = true)
    public void sendFeeReminder(Long feeId) {
        Fees fees = getFeeById(feeId);
        if (!sendReminderFor(fees)) {
            log.info("No reminder sent for fee id {} (nothing due or no email on file)", feeId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public int sendReminders(String academicYear) {
        List<Fees> candidates = (academicYear == null || academicYear.isBlank())
                ? feesRepository.findAll()
                : feesRepository.findByAcademicYear(academicYear);

        int sent = 0;
        for (Fees fees : candidates) {
            if (sendReminderFor(fees)) sent++;
        }
        log.info("Dispatched {} fee reminder email(s){}", sent,
                (academicYear == null || academicYear.isBlank()) ? "" : " for " + academicYear);
        return sent;
    }

    /** Sends a reminder for one fee record if it has an outstanding balance and a valid email. Returns true if dispatched. */
    private boolean sendReminderFor(Fees fees) {
        BigDecimal due = fees.getDueAmount() != null ? fees.getDueAmount() : BigDecimal.ZERO;
        if (due.compareTo(BigDecimal.ZERO) <= 0) return false;

        Student student = fees.getStudent();
        if (student == null) return false;
        String email = student.getEmail();
        if (email == null || !email.contains("@")) return false;

        String dueDate = fees.getDueDate() != null ? fees.getDueDate().toString() : null;
        EmailTemplates.Email mail = EmailTemplates.feeReminder(
                student.getName(), student.getClassName(), fees.getAcademicYear(),
                AMOUNT_FMT.format(due), dueDate);
        emailService.sendEmail(email, mail.subject(), mail.html(), mail.text());
        return true;
    }
}