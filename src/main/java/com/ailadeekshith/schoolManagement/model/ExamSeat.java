package com.ailadeekshith.schoolManagement.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * A single seat position within an {@link ExamSeatingPlan}, optionally occupied
 * by a student.
 */
@Entity
@Table(name = "exam_seat", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"plan_id", "row_num", "col_num", "seat_index"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private ExamSeatingPlan plan;

    @Column(name = "row_num", nullable = false)
    private Integer rowNum;

    @Column(name = "col_num", nullable = false)
    private Integer colNum;

    @Column(name = "seat_index", nullable = false)
    private Integer seatIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;
}
