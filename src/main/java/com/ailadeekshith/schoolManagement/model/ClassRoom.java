package com.ailadeekshith.schoolManagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "classes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Class name is required")
    @Column(name = "class_name", nullable = false, unique = true)
    private String className;         // e.g. "10-A"

    @NotBlank(message = "Room number is required")
    @Column(name = "room_number", nullable = false)
    private String roomNumber;

    @Column(name = "max_capacity")
    @Builder.Default
    private Integer maxCapacity = 40;

    @Column(name = "current_strength")
    @Builder.Default
    private Integer currentStrength = 0;

    @Column(name = "class_monitor")
    private String classMonitor;

    @Column(name = "rows")
    private Integer rows;

    @Column(name = "columns")
    private Integer columns;

    @Column(name = "students_per_bench")
    @Builder.Default
    private Integer studentsPerBench = 2;

    // ── Class teacher FK ─────────────────────────────────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_teacher_id")
    private Teacher classTeacher;

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
}