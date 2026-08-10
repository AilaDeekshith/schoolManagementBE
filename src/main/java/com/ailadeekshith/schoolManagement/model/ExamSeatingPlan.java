package com.ailadeekshith.schoolManagement.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * A seating plan for one room-sitting of an exam: a hall hosts sittings across
 * multiple days and multiple sessions per day, so a plan is scoped to a
 * (room + date + session) and has an invigilator assigned to it.
 */
@Entity
@Table(name = "exam_seating_plan")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamSeatingPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @Column(name = "room_name", nullable = false)
    private String roomName;

    // A room hosts many sessions (time periods), each with its own invigilator.
    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private java.util.List<ExamSession> sessions = new java.util.ArrayList<>();

    @Column(name = "rows_count")
    private Integer rows;

    @Column(name = "cols_count")
    private Integer columns;

    @Column(name = "seats_per_bench")
    @Builder.Default
    private Integer seatsPerBench = 1;

    /** Comma-separated class names (e.g. "10-A,10-B") whose students sit here. */
    @Column(name = "class_names", columnDefinition = "TEXT")
    private String classNames;

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
