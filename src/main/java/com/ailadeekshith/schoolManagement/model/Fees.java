package com.ailadeekshith.schoolManagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "fees")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Fees {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ── Student FK ───────────────────────────────────────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @NotNull(message = "Student is required")
    private Student student;

    // ── Fee details ───────────────────────────────────────────
    @NotNull(message = "Total amount is required")
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "paid_amount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Column(name = "due_amount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal dueAmount = BigDecimal.ZERO;

    @Column(name = "academic_year")
    private String academicYear;      // e.g. "2024-25"

    @Column(name = "fee_type")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private FeeType feeType = FeeType.TUITION;

    @Enumerated(EnumType.STRING)
    @Column(name = "fee_status", nullable = false)
    @Builder.Default
    private FeeStatus feeStatus = FeeStatus.PENDING;

    @Column(name = "due_date")
    private LocalDate dueDate;

    // ── Payment ───────────────────────────────────────────────
    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "payment_method")
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(name = "transaction_id")
    private String transactionId;

    // ── Audit ─────────────────────────────────────────────────
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        // auto-compute due
        if (totalAmount != null && paidAmount != null) {
            dueAmount = totalAmount.subtract(paidAmount);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        if (totalAmount != null && paidAmount != null) {
            dueAmount = totalAmount.subtract(paidAmount);
            if (dueAmount.compareTo(BigDecimal.ZERO) <= 0) {
                feeStatus = FeeStatus.PAID;
            }
        }
    }

    // ── Enums ─────────────────────────────────────────────────
    public enum FeeStatus     { PAID, PENDING, OVERDUE }
    public enum FeeType       { TUITION, TRANSPORT, EXAM, LIBRARY, SPORTS, OTHER }
    public enum PaymentMethod { CASH, CHEQUE, ONLINE_TRANSFER, UPI, DEMAND_DRAFT }
}