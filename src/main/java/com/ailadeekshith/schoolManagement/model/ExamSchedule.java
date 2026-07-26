package com.ailadeekshith.schoolManagement.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

/**
 * A single paper in an exam's timetable — an exam (e.g. "Mid Term") spans
 * several days, each row here is one subject scheduled on a date & time.
 */
@Entity
@Table(name = "exam_schedule")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @Column(name = "subject")
    private String subject;

    @Column(name = "exam_date")
    private LocalDate examDate;

    // Stored as "HH:mm" strings to keep it simple on both ends.
    @Column(name = "start_time")
    private String startTime;

    @Column(name = "end_time")
    private String endTime;

    @Column(name = "class_name")
    private String className;

    @Column(name = "max_marks")
    private Integer maxMarks;

    @Column(name = "room")
    private String room;

    @Column(name = "notes")
    private String notes;
}
