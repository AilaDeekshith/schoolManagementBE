package com.ailadeekshith.schoolManagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "timetables",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"class_name", "day_of_week", "period_number"}
        ))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Class name is required")
    @Column(name = "class_name", nullable = false)
    private String className;

    @NotNull(message = "Day of week is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @NotNull(message = "Period number is required")
    @Column(name = "period_number", nullable = false)
    private Integer periodNumber;

    @NotBlank(message = "Subject is required")
    @Column(nullable = false)
    private String subject;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    // ── Teacher FK ───────────────────────────────────────────
    // JsonIgnoreProperties prevents infinite recursion during serialization
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Teacher teacher;

    // ── Transient field to receive teacherId from JSON body ──
    // Jackson will populate this from {"teacherId": 7}
    // The service then looks up the Teacher entity by this id
    @Transient
    private Long teacherId;

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

    // ── Convenience getter — returns teacher id from relation ─
    // Used during serialization so the response includes teacherId
    public Long getTeacherId() {
        if (teacherId != null) return teacherId;
        return teacher != null ? teacher.getId() : null;
    }

    // ── Enum ──────────────────────────────────────────────────
    public enum DayOfWeek { MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY }
}