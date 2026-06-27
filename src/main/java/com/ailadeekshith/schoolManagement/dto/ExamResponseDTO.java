package com.ailadeekshith.schoolManagement.dto;
import com.ailadeekshith.schoolManagement.model.Exam;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamResponseDTO {

    private Long id;
    private String name;
    private String subject;
    private String className;
    private LocalDate examDate;
    private Integer maxMarks;
    private String duration;
    private String instructions;
    private Exam.ExamStatus status;

    // Flattened examiner info
    private Long examinerId;
    private String examinerName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}