package com.ailadeekshith.schoolManagement.dto;
import com.ailadeekshith.schoolManagement.model.TimeTable;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeTableRequestDTO {

    @NotBlank(message = "Class name is required")
    private String className;

    @NotNull(message = "Day of week is required")
    private TimeTable.DayOfWeek dayOfWeek;

    @NotNull(message = "Period number is required")
    @Min(value = 1, message = "Period number must be at least 1")
    @Max(value = 8, message = "Period number cannot exceed 8")
    private Integer periodNumber;

    @NotBlank(message = "Subject is required")
    private String subject;

    private LocalTime startTime;
    private LocalTime endTime;
    private Long teacherId;           // FK
}