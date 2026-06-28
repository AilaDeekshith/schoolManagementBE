package com.ailadeekshith.schoolManagement.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "holidays")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Holiday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // "Republic Day", "Summer Vacation"

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "end_date")
    private LocalDate endDate; // null = single day

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private HolidayType type = HolidayType.PUBLIC;

    private String description;

    @Column(name = "academic_year")
    private String academicYear; // "2024-25"

    public enum HolidayType { PUBLIC, SCHOOL, VACATION, EXAM_BREAK, FESTIVAL }
}
