package com.ailadeekshith.schoolManagement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * A teacher's end-of-period summary for one specific class occurrence: the
 * topic (and sub-topics) actually covered, any homework set, and instructions
 * for the next class. Tied to the recurring {@link TimeTable} slot it logs
 * plus the specific calendar date it happened on — one entry per
 * (timetable slot, date).
 */
@Entity
@Table(name = "class_diary_entries", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"timetable_id", "date"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassDiaryEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The recurring weekly slot this entry logs (gives us class/subject/day/period).
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timetable_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private TimeTable timetable;

    // Denormalized so the class/subject survive even if the timetable slot is
    // later reassigned or removed, and so queries don't always need a join.
    @NotBlank(message = "Class name is required")
    @Column(name = "class_name", nullable = false)
    private String className;

    @NotBlank(message = "Subject is required")
    @Column(nullable = false)
    private String subject;

    @NotNull(message = "Date is required")
    @Column(nullable = false)
    private LocalDate date;

    // Who actually logged/taught this occurrence — captured at entry time so a
    // substitute teaching one day doesn't get misattributed to the regular one.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Teacher teacher;

    @NotBlank(message = "Topic covered is required")
    @Column(name = "topic_covered", nullable = false, columnDefinition = "TEXT")
    private String topicCovered;

    @Column(name = "sub_topics", columnDefinition = "TEXT")
    private String subTopics;

    @Column(columnDefinition = "TEXT")
    private String homework;

    @Column(name = "instructions_for_next", columnDefinition = "TEXT")
    private String instructionsForNext;

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
