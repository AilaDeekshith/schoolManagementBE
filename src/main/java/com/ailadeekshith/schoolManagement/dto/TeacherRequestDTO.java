package com.ailadeekshith.schoolManagement.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherRequestDTO {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Subject is required")
    private String subject;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Contact number is required")
    private String contactNumber;

    private String qualification;
    private String experience;
    private String assignedClasses;
    private String status;
}