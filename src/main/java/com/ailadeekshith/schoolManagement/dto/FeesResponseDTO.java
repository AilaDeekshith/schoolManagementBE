package com.ailadeekshith.schoolManagement.dto;
import com.ailadeekshith.schoolManagement.model.Fees;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeesResponseDTO {

    private Long id;

    // Flattened student info
    private Long studentId;
    private String studentName;
    private String studentClass;

    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal dueAmount;
    private String academicYear;
    private Fees.FeeType feeType;
    private Fees.FeeStatus feeStatus;
    private LocalDate dueDate;
    private LocalDate paymentDate;
    private Fees.PaymentMethod paymentMethod;
    private String transactionId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}