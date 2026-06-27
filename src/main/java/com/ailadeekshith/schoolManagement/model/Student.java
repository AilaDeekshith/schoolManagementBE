package com.ailadeekshith.schoolManagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Student name is required")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "Date of birth is required")
    @Column(name = "dob", nullable = false)
    private LocalDate dob;

    @NotBlank(message = "Gender is required")
    private String gender;

    @Column(name = "blood_group")
    private String bloodGroup;

    @Column(columnDefinition = "TEXT")
    private String address;

    private String nationality;

    private String religion;

    @Email(message = "Invalid email format")
    @Column(unique = true)
    private String email;

    // ── Academic ──────────────────────────────────────────────
    @NotBlank(message = "Class is required")
    @Column(name = "class_name", nullable = false)
    private String className;          // e.g. "10-A"

    @Column(name = "roll_number")
    private Integer rollNumber;

    @Column(name = "admission_date")
    private LocalDate admissionDate;

    @Column(name = "bus_route")
    private String busRoute;

    @Column(name = "medical_notes", columnDefinition = "TEXT")
    private String medicalNotes;

    // ── Guardian ──────────────────────────────────────────────
    @NotBlank(message = "Guardian name is required")
    @Column(name = "guardian_name", nullable = false)
    private String guardianName;

    @NotBlank(message = "Contact number is required")
    @Column(name = "contact_number", nullable = false)
    private String contactNumber;

    // ── Status ────────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StudentStatus status = StudentStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(name = "fee_status")
    @Builder.Default
    private FeeStatus feeStatus = FeeStatus.PENDING;

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

    // ── Enums ─────────────────────────────────────────────────
    public enum StudentStatus { ACTIVE, INACTIVE }
    public enum FeeStatus     { PAID, PENDING, OVERDUE }
}