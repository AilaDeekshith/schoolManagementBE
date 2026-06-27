package com.ailadeekshith.schoolManagement.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamRequestDTO {

    @NotBlank(message = "Exam name is required")
    private String name;

    @NotBlank(message = "Subject is required")
    private String subject;

    private String className;

    @NotNull(message = "Exam date is required")
    private LocalDate examDate;

    @NotNull(message = "Maximum marks are required")
    @Min(value = 1, message = "Max marks must be at least 1")
    private Integer maxMarks;

    private String duration;
    private String instructions;
    private Long examinerId;          // FK
}