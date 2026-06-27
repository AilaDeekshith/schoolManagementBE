package com.ailadeekshith.schoolManagement.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassRoomRequestDTO {

    @NotBlank(message = "Class name is required")
    private String className;

    @NotBlank(message = "Room number is required")
    private String roomNumber;

    private Integer maxCapacity;
    private String classMonitor;
    private Long classTeacherId;      // FK — teacher assigned as class teacher
}