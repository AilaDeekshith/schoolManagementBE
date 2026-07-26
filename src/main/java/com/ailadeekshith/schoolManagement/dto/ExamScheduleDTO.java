package com.ailadeekshith.schoolManagement.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamScheduleDTO {
    private Long id;
    private Long examId;
    private String examName;
    private String subject;
    private LocalDate examDate;
    private String startTime;
    private String endTime;
    private String className;
    private Integer maxMarks;
    private String room;
    private String notes;
}
