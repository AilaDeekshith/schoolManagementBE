package com.ailadeekshith.schoolManagement.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "school_profile")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchoolProfile {

    @Id
    @Builder.Default
    private Long id = 1L; // singleton — always one record

    @Column(name = "school_name")
    private String schoolName;

    private String address;

    private String phone;

    private String email;

    @Column(name = "principal_name")
    private String principalName;

    @Column(name = "academic_year")
    private String academicYear; // e.g. "2024-25"

    @Column(name = "established_year")
    private Integer establishedYear;

    private String website;

    @Column(name = "affiliation_board")
    private String affiliationBoard; // CBSE, ICSE, State Board, IB, etc.

    @Column(name = "school_type")
    private String schoolType; // Government, Private, Aided
}
