package com.ailadeekshith.schoolManagement.dto;
import com.ailadeekshith.schoolManagement.model.TimeTable;
import lombok.*;

import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeTableResponseDTO {

    private Long id;
    private String className;
    private TimeTable.DayOfWeek dayOfWeek;
    private Integer periodNumber;
    private String subject;
    private LocalTime startTime;
    private LocalTime endTime;

    // Flattened teacher info
    private Long teacherId;
    private String teacherName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}