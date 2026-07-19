package com.ailadeekshith.schoolManagement.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamSeatingPlanDTO {
    private Long id;
    private Long examId;
    private String examName;
    private String examSubject;
    private LocalDate examDate;
    private String roomName;
    private Integer rows;
    private Integer columns;
    private Integer seatsPerBench;
    private List<String> classNames;
    private Integer totalSeats;
    private Integer seatedCount;
}
