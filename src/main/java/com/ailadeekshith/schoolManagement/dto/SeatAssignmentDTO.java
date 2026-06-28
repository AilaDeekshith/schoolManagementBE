package com.ailadeekshith.schoolManagement.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatAssignmentDTO {
    private Long id;
    private Integer rowNum;
    private Integer colNum;
    private Integer seatIndex;
    private Long studentId;
    private String studentName;
}
