package com.ailadeekshith.schoolManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TimeTablePeriodRequestDto {

    private LocalTime endTime;
    private boolean isBreak;
    private String notes;
    private String periodLabel;
    private LocalTime startTime;
}
