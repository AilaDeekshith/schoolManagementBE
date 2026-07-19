package com.ailadeekshith.schoolManagement.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamSeatingPlanRequest {
    private Long examId;
    private String roomName;
    private Integer rows;
    private Integer columns;
    private Integer seatsPerBench;
    private List<String> classNames;
}
