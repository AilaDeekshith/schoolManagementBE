package com.ailadeekshith.schoolManagement.dto;
import com.ailadeekshith.schoolManagement.model.Fees;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeesRequestDTO {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    private BigDecimal totalAmount;

    @Builder.Default
    private BigDecimal paidAmount = BigDecimal.ZERO;

    private String academicYear;
    private Fees.FeeType feeType;
    private LocalDate dueDate;
}