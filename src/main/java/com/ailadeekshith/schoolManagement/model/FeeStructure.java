package com.ailadeekshith.schoolManagement.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "fee_structure")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeeStructure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "grade_name", nullable = false)
    private String gradeName; // "Grade 10", "All Grades"

    @Enumerated(EnumType.STRING)
    @Column(name = "fee_category", nullable = false)
    private FeeCategory feeCategory;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "academic_year", nullable = false)
    private String academicYear; // "2024-25"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Frequency frequency = Frequency.ANNUAL;

    private String description;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    public enum FeeCategory { TUITION, TRANSPORT, LABORATORY, LIBRARY, SPORTS, EXAM, OTHER }
    public enum Frequency   { ANNUAL, TERM, MONTHLY }
}
