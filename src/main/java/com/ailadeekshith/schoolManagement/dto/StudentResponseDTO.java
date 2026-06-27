package com.ailadeekshith.schoolManagement.dto;

import com.ailadeekshith.schoolManagement.model.Student;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentResponseDTO {

    private Long id;
    private String name;
    private LocalDate dob;
    private String gender;
    private String bloodGroup;
    private String address;
    private String nationality;
    private String religion;
    private String email;
    private String className;
    private Integer rollNumber;
    private LocalDate admissionDate;
    private String busRoute;
    private String medicalNotes;
    private String guardianName;
    private String contactNumber;
    private Student.StudentStatus status;
    private Student.FeeStatus feeStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}