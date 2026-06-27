package com.ailadeekshith.schoolManagement.dto;

import com.ailadeekshith.schoolManagement.model.Teacher;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherResponseDTO {

    private Long id;
    private String name;
    private String subject;
    private String email;
    private String contactNumber;
    private String qualification;
    private String experience;
    private String assignedClasses;
    private Teacher.TeacherStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}