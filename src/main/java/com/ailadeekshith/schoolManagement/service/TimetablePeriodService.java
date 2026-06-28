package com.ailadeekshith.schoolManagement.service;


import com.ailadeekshith.schoolManagement.dto.TimeTablePeriodRequestDto;
import com.ailadeekshith.schoolManagement.model.TimetablePeriod;

import java.util.List;

public interface TimetablePeriodService {
    List<TimetablePeriod> savePeriodsForClass(String className, List<TimeTablePeriodRequestDto> periods);
    List<TimetablePeriod> getPeriodsForClass(String className);
    void deletePeriodsForClass(String className);
    TimetablePeriod updatePeriod(Long id, TimetablePeriod period);
}