package com.ailadeekshith.schoolManagement.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdmissionRequestDTO {

    @NotBlank(message = "Applicant name is required")
    private String applicantName;

    private LocalDate dob;
    private String gender;

    @NotBlank(message = "Applying class is required")
    private String applyClass;

    @NotBlank(message = "Guardian name is required")
    private String guardianName;

    @NotBlank(message = "Contact number is required")
    private String contactNumber;

    @Email(message = "Invalid email format")
    private String guardianEmail;

    private String prevSchool;
    private String reason;
}