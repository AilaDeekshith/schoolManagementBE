package com.ailadeekshith.schoolManagement.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentRequestDTO {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Date of birth is required")
    private LocalDate dob;

    @NotBlank(message = "Gender is required")
    private String gender;

    private String bloodGroup;
    private String address;
    private String nationality;
    private String religion;

    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Class is required")
    private String className;

    private Integer rollNumber;
    private LocalDate admissionDate;
    private String busRoute;
    private String medicalNotes;

    @NotBlank(message = "Guardian name is required")
    private String guardianName;

    @NotBlank(message = "Contact number is required")
    private String contactNumber;

    private String status;
}