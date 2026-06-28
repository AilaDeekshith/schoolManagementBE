package com.ailadeekshith.schoolManagement.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
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

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
}
