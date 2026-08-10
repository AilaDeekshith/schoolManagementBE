package com.ailadeekshith.schoolManagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "exams")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Exam name is required")
    @Column(nullable = false)
    private String name;              // e.g. "Unit Test 1"

    @NotBlank(message = "Subject is required")
    @Column(nullable = false)
    private String subject;

    @Column(name = "class_name")
    private String className;         // "All" or specific class

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Section section;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Subject subjectRef;

    @NotNull(message = "Exam date is required")
    @Column(name = "exam_date", nullable = false)
    private LocalDate examDate;

    @NotNull(message = "Maximum marks are required")
    @Column(name = "max_marks", nullable = false)
    private Integer maxMarks;

    private String duration;          // e.g. "3 hours"

    @Column(columnDefinition = "TEXT")
    private String instructions;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ExamStatus status = ExamStatus.SCHEDULED;

    // ── Audit ─────────────────────────────────────────────────
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ── Enum ──────────────────────────────────────────────────
    public enum ExamStatus { SCHEDULED, UPCOMING, COMPLETED, CANCELLED }
}