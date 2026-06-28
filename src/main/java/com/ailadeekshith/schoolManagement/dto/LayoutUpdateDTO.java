package com.ailadeekshith.schoolManagement.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LayoutUpdateDTO {
    private Integer rows;
    private Integer columns;
    private Integer studentsPerBench;
}
