package com.ailadeekshith.schoolManagement.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamSeatDTO {
    private Long id;
    private Integer rowNum;
    private Integer colNum;
    private Integer seatIndex;
    private Long studentId;
    private String studentName;
    private String studentClass;
    private Integer rollNumber;
}
