package com.ailadeekshith.schoolManagement.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamSessionDTO {
    private Long id;
    private Long planId;
    private LocalDate examDate;
    private String startTime;
    private String endTime;
    private Long invigilatorId;
    private String invigilatorName;
}
