package com.ailadeekshith.schoolManagement.controller;

import com.ailadeekshith.schoolManagement.dto.TimeTablePeriodRequestDto;
import com.ailadeekshith.schoolManagement.model.TimetablePeriod;
import com.ailadeekshith.schoolManagement.service.TimetablePeriodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/timetable/periods")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TimetablePeriodController {

    private final TimetablePeriodService periodService;

    // POST /api/timetable/periods/{className}
    // Body: array of period objects — replaces existing structure for that class
    @PostMapping("/{className}")
    public ResponseEntity<List<TimetablePeriod>> savePeriods(
            @PathVariable String className,
            @RequestBody @Valid List<TimeTablePeriodRequestDto> periods) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(periodService.savePeriodsForClass(className, periods));
    }

    // GET /api/timetable/periods/{className}
    @GetMapping("/{className}")
    public ResponseEntity<List<TimetablePeriod>> getPeriods(@PathVariable String className) {
        return ResponseEntity.ok(periodService.getPeriodsForClass(className));
    }

    // PUT /api/timetable/periods/entry/{id}
    @PutMapping("/entry/{id}")
    public ResponseEntity<TimetablePeriod> updatePeriod(
            @PathVariable Long id,
            @RequestBody @Valid TimetablePeriod period) {
        return ResponseEntity.ok(periodService.updatePeriod(id, period));
    }

    // DELETE /api/timetable/periods/{className}
    @DeleteMapping("/{className}")
    public ResponseEntity<Void> deletePeriods(@PathVariable String className) {
        periodService.deletePeriodsForClass(className);
        return ResponseEntity.noContent().build();
    }
}