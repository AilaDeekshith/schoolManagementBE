package com.ailadeekshith.schoolManagement.service.impl;

import com.ailadeekshith.schoolManagement.dto.TimeTablePeriodRequestDto;
import com.ailadeekshith.schoolManagement.exception.ResourceNotFoundException;
import com.ailadeekshith.schoolManagement.model.TimetablePeriod;
import com.ailadeekshith.schoolManagement.repository.TimetablePeriodRepository;
import com.ailadeekshith.schoolManagement.service.TimetablePeriodService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TimetablePeriodServiceImpl implements TimetablePeriodService {

    private final TimetablePeriodRepository periodRepository;

    @Override
    public List<TimetablePeriod> savePeriodsForClass(String className, List<TimeTablePeriodRequestDto> periods) {
        log.info("Saving {} periods for class: {}", periods.size(), className);

        // Delete existing structure before saving the new one
        periodRepository.deleteAllByClassName(className);

        List<TimetablePeriod> timetablePeriodList = new ArrayList<>();

        for(TimeTablePeriodRequestDto timeTablePeriodRequestDto: periods) {
            TimetablePeriod timetablePeriod = new TimetablePeriod();
            timetablePeriod.setPeriodLabel(timeTablePeriodRequestDto.getPeriodLabel());
            timetablePeriod.setStartTime(timeTablePeriodRequestDto.getStartTime());
            timetablePeriod.setEndTime(timeTablePeriodRequestDto.getEndTime());
            timetablePeriod.setIsBreak(timeTablePeriodRequestDto.isBreak());
            timetablePeriod.setNotes(timeTablePeriodRequestDto.getNotes());
            timetablePeriodList.add(timetablePeriod);
        }

        // Auto-assign period numbers in order
        AtomicInteger counter = new AtomicInteger(1);
        timetablePeriodList.forEach(p -> {
            p.setClassName(className);
            p.setPeriodNumber(counter.getAndIncrement());
            p.setId(null); // ensure insert not update
        });

        return periodRepository.saveAll(timetablePeriodList);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimetablePeriod> getPeriodsForClass(String className) {
        return periodRepository.findByClassNameOrderByPeriodNumberAsc(className);
    }

    @Override
    public void deletePeriodsForClass(String className) {
        log.info("Deleting all periods for class: {}", className);
        periodRepository.deleteAllByClassName(className);
    }

    @Override
    public TimetablePeriod updatePeriod(Long id, TimetablePeriod updated) {
        TimetablePeriod existing = periodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Period not found with id: " + id));
        existing.setPeriodLabel(updated.getPeriodLabel());
        existing.setStartTime(updated.getStartTime());
        existing.setEndTime(updated.getEndTime());
        existing.setIsBreak(updated.getIsBreak());
        existing.setNotes(updated.getNotes());
        return periodRepository.save(existing);
    }
}