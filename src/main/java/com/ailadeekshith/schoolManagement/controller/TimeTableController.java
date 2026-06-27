package com.ailadeekshith.schoolManagement.controller;

import com.ailadeekshith.schoolManagement.model.TimeTable;
import com.ailadeekshith.schoolManagement.service.TimeTableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/timetable")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TimeTableController {

    private final TimeTableService timeTableService;

    // POST /api/timetable
    @PostMapping
    public ResponseEntity<TimeTable> createEntry(@Valid @RequestBody TimeTable timeTable) {
        return ResponseEntity.status(HttpStatus.CREATED).body(timeTableService.createEntry(timeTable));
    }

    // GET /api/timetable
    @GetMapping
    public ResponseEntity<List<TimeTable>> getAllEntries() {
        return ResponseEntity.ok(timeTableService.getAllEntries());
    }

    // GET /api/timetable/{id}
    @GetMapping("/{id}")
    public ResponseEntity<TimeTable> getEntryById(@PathVariable Long id) {
        return ResponseEntity.ok(timeTableService.getEntryById(id));
    }

    // PUT /api/timetable/{id}
    @PutMapping("/{id}")
    public ResponseEntity<TimeTable> updateEntry(@PathVariable Long id,
                                                 @Valid @RequestBody TimeTable timeTable) {
        return ResponseEntity.ok(timeTableService.updateEntry(id, timeTable));
    }

    // DELETE /api/timetable/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEntry(@PathVariable Long id) {
        timeTableService.deleteEntry(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/timetable/class/{className}
    @GetMapping("/class/{className}")
    public ResponseEntity<List<TimeTable>> getByClass(@PathVariable String className) {
        return ResponseEntity.ok(timeTableService.getScheduleByClass(className));
    }

    // GET /api/timetable/class/{className}/day/{day}
    @GetMapping("/class/{className}/day/{day}")
    public ResponseEntity<List<TimeTable>> getByClassAndDay(@PathVariable String className,
                                                            @PathVariable TimeTable.DayOfWeek day) {
        return ResponseEntity.ok(timeTableService.getScheduleByClassAndDay(className, day));
    }

    // GET /api/timetable/teacher/{teacherId}
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<TimeTable>> getByTeacher(@PathVariable Long teacherId) {
        return ResponseEntity.ok(timeTableService.getScheduleByTeacher(teacherId));
    }

    // GET /api/timetable/teacher/{teacherId}/day/{day}
    @GetMapping("/teacher/{teacherId}/day/{day}")
    public ResponseEntity<List<TimeTable>> getTeacherDaySchedule(@PathVariable Long teacherId,
                                                                 @PathVariable TimeTable.DayOfWeek day) {
        return ResponseEntity.ok(timeTableService.getTeacherScheduleForDay(teacherId, day));
    }
}
