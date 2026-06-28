package com.ailadeekshith.schoolManagement.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subjects")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name; // "Mathematics"

    private String code; // "MATH"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private SubjectType type = SubjectType.CORE;

    private String description;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    public enum SubjectType { CORE, ELECTIVE, CO_CURRICULAR }
}
