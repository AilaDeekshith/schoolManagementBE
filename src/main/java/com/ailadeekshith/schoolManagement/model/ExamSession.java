package com.ailadeekshith.schoolManagement.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

/**
 * One sitting (time period) held in an exam room. A room can host many
 * sessions across days; each session has its own time slot and invigilator.
 */
@Entity
@Table(name = "exam_session")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private ExamSeatingPlan plan;

    @Column(name = "exam_date")
    private LocalDate examDate;

    // "HH:mm" strings for the time period.
    @Column(name = "start_time")
    private String startTime;

    @Column(name = "end_time")
    private String endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invigilator_id")
    private Teacher invigilator;
}
