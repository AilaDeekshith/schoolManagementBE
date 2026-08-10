package com.ailadeekshith.schoolManagement.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sections", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"grade_id", "letter"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String letter; // "A", "B", "C", "D"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_id", nullable = false)
    @JsonBackReference
    private Grade grade;

    // A section (the "class", e.g. Grade 10 - A) has a class teacher and is
    // assigned a physical room. @JsonIgnore keeps the grade tree lightweight —
    // these are surfaced via SectionDTO instead.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_teacher_id")
    @JsonIgnore
    private Teacher classTeacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    @JsonIgnore
    private ClassRoom room;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
}
