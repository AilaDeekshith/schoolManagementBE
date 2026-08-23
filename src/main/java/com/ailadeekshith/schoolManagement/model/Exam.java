package com.ailadeekshith.schoolManagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    // The subjects this exam covers (e.g. ["Mathematics", "Physics"]).
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "exam_subjects", joinColumns = @JoinColumn(name = "exam_id"))
    @Column(name = "subject")
    @Builder.Default
    private List<String> subjects = new ArrayList<>();

    // The classes this exam is conducted for (e.g. ["10-A", "10-B"]).
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "exam_classes", joinColumns = @JoinColumn(name = "exam_id"))
    @Column(name = "class_name")
    @Builder.Default
    private List<String> classes = new ArrayList<>();

    // The academic year this exam belongs to (e.g. "2024-25").
    @Column(name = "academic_year")
    private String academicYear;

    // Optional overall exam date; per-paper dates live in the exam timetable.
    @Column(name = "exam_date")
    private LocalDate examDate;

    // Optional default max marks; per-paper marks live in the exam timetable.
    @Column(name = "max_marks")
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