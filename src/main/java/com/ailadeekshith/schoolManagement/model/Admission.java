package com.ailadeekshith.schoolManagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "admissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Admission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ── Applicant ─────────────────────────────────────────────
    @NotBlank(message = "Applicant name is required")
    @Column(name = "applicant_name", nullable = false)
    private String applicantName;

    @Column(name = "dob")
    private LocalDate dob;

    private String gender;

    @NotBlank(message = "Applying class is required")
    @Column(name = "apply_class", nullable = false)
    private String applyClass;        // e.g. "9-A"

    // ── Guardian ──────────────────────────────────────────────
    @NotBlank(message = "Guardian name is required")
    @Column(name = "guardian_name", nullable = false)
    private String guardianName;

    @NotBlank(message = "Contact number is required")
    @Column(name = "contact_number", nullable = false)
    private String contactNumber;

    @Email(message = "Invalid email format")
    @Column(name = "guardian_email")
    private String guardianEmail;

    // ── Previous school ───────────────────────────────────────
    @Column(name = "prev_school")
    private String prevSchool;

    @Column(columnDefinition = "TEXT")
    private String reason;

    // ── Status ────────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AdmissionStatus status = AdmissionStatus.PENDING;

    @Column(name = "applied_date")
    @Builder.Default
    private LocalDate appliedDate = LocalDate.now();

    // ── Link to student once approved ─────────────────────────
    @OneToOne
    @JoinColumn(name = "student_id")
    private Student student;

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
    public enum AdmissionStatus { PENDING, UNDER_REVIEW, APPROVED, REJECTED }
}