package com.ailadeekshith.schoolManagement.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectionDTO {
    private Long id;
    private String letter;
    private Boolean isActive;
    private Long classTeacherId;
    private String classTeacherName;
    private Long roomId;
    private String roomNumber;
}
