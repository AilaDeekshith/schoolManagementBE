package com.ailadeekshith.schoolManagement.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassRoomResponseDTO {

    private Long id;
    private String className;
    private String roomNumber;
    private Integer maxCapacity;
    private Integer currentStrength;
    private String classMonitor;

    // Flattened teacher info (avoid full nested object)
    private Long classTeacherId;
    private String classTeacherName;
    private String classTeacherSubject;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}