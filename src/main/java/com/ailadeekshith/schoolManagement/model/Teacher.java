package com.ailadeekshith.schoolManagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "teachers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Teacher name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Subject is required")
    @Column(nullable = false)
    private String subject;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Contact number is required")
    @Column(name = "contact_number", nullable = false)
    private String contactNumber;

    private String qualification;

    private String experience;       // e.g. "8 yrs"

    @Column(name = "assigned_classes")
    private String assignedClasses;  // e.g. "10-A, 9-B"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TeacherStatus status = TeacherStatus.ACTIVE;

    @Column(name = "photo_base64", columnDefinition = "TEXT")
    private String photoBase64;

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
    public enum TeacherStatus { ACTIVE, ON_LEAVE, INACTIVE }
}