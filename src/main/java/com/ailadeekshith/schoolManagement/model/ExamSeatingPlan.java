package com.ailadeekshith.schoolManagement.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * A seating plan for a single room/hall used during an exam. Each plan belongs
 * to one {@link Exam} and defines the room layout (rows × columns × seats per
 * bench) plus the classes/sections whose students sit in this room.
 */
@Entity
@Table(name = "exam_seating_plan", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"exam_id", "room_name"})
})
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
