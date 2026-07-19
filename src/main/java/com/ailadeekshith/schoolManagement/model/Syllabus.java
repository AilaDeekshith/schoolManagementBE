package com.ailadeekshith.schoolManagement.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "syllabi",
       uniqueConstraints = @UniqueConstraint(columnNames = {"grade_name", "subject_name", "academic_year"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Syllabus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "grade_name", nullable = false)
    private String gradeName;          // e.g. "Grade 9"

    @Column(name = "subject_name", nullable = false)
    private String subjectName;        // e.g. "Physics"

    @Column(name = "academic_year")
    private String academicYear;       // e.g. "2024-25"

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "total_hours")
    private Integer totalHours;

    @OneToMany(mappedBy = "syllabus", cascade = CascadeType.ALL,
               orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("orderIndex ASC")
    @JsonManagedReference("syllabus-topics")
    @Builder.Default
    private List<SyllabusTopic> topics = new ArrayList<>();
}
