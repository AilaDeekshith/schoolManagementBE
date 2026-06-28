package com.ailadeekshith.schoolManagement.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "grades")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name; // e.g. "Grade 1", "Class 10"

    @Column(name = "display_order")
    private Integer displayOrder;

    @OneToMany(mappedBy = "grade", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    @Builder.Default
    private List<Section> sections = new ArrayList<>();
}
