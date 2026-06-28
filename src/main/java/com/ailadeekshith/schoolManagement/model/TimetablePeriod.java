package com.ailadeekshith.schoolManagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "timetable_periods",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"class_name", "period_number"}
        ))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimetablePeriod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Class name is required")
    @Column(name = "class_name", nullable = false)
    private String className;

    @NotNull(message = "Period number is required")
    @Column(name = "period_number", nullable = false)
    private Integer periodNumber;

    @NotBlank(message = "Period label is required")
    @Column(name = "period_label", nullable = false)
    private String periodLabel;       // e.g. "P1", "Break", "Lunch"

    @NotNull(message = "Start time is required")
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "is_break")
    @Builder.Default
    private Boolean isBreak = false;  // true = break/lunch, no subject assignment

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(columnDefinition = "TEXT")
    private String notes;             // e.g. "Assembly period on Mondays"

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        computeDuration();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        computeDuration();
    }

    private void computeDuration() {
        if (startTime != null && endTime != null) {
            durationMinutes = (int) java.time.Duration
                    .between(startTime, endTime).toMinutes();
        }
    }
}