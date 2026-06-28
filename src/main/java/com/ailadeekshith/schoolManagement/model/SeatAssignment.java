package com.ailadeekshith.schoolManagement.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "seat_assignments", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"class_room_id", "row_num", "col_num", "seat_index"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_room_id", nullable = false)
    private ClassRoom classRoom;

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
