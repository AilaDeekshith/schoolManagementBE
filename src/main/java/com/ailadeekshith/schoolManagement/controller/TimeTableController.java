package com.ailadeekshith.schoolManagement.controller;

import com.ailadeekshith.schoolManagement.dto.TimeTableResponseDTO;
import com.ailadeekshith.schoolManagement.model.TimeTable;
import com.ailadeekshith.schoolManagement.service.TimeTableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/timetable")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TimeTableController {

    private final TimeTableService timeTableService;

    // ── Map entity → DTO ─────────────────────────────────────
    // Flattens the teacher relationship so the response contains
    // teacherId and teacherName instead of a nested Teacher object.
    private TimeTableResponseDTO toDTO(TimeTable t) {
        TimeTableResponseDTO.TimeTableResponseDTOBuilder b = TimeTableResponseDTO.builder()
                .id(t.getId())
                .className(t.getClassName())
                .dayOfWeek(t.getDayOfWeek())
                .periodNumber(t.getPeriodNumber())
                .subject(t.getSubject())
                .startTime(t.getStartTime())
                .endTime(t.getEndTime())
                .createdAt(t.getCreatedAt())
                .updatedAt(t.getUpdatedAt());

        // Flatten teacher — never null-pointer even if teacher is null
        if (t.getTeacher() != null) {
            b.teacherId(t.getTeacher().getId())
                    .teacherName(t.getTeacher().getName())
                    .teacherSubject(t.getTeacher().getSubject());
        }

        return b.build();
    }

    // POST /api/timetable
    @PostMapping
    public ResponseEntity<TimeTableResponseDTO> createEntry(@Valid @RequestBody TimeTable timeTable) {
        TimeTable saved = timeTableService.createEntry(timeTable);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(saved));
    }

    // GET /api/timetable
    @GetMapping
    public ResponseEntity<List<TimeTableResponseDTO>> getAllEntries() {
        return ResponseEntity.ok(
                timeTableService.getAllEntries().stream().map(this::toDTO).collect(Collectors.toList())
        );
    }

    // GET /api/timetable/{id}
    @GetMapping("/{id}")
    public ResponseEntity<TimeTableResponseDTO> getEntryById(@PathVariable Long id) {
        return ResponseEntity.ok(toDTO(timeTableService.getEntryById(id)));
    }

    // PUT /api/timetable/{id}
    @PutMapping("/{id}")
    public ResponseEntity<TimeTableResponseDTO> updateEntry(
            @PathVariable Long id,
            @Valid @RequestBody TimeTable timeTable) {
        return ResponseEntity.ok(toDTO(timeTableService.updateEntry(id, timeTable)));
    }

    // DELETE /api/timetable/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEntry(@PathVariable Long id) {
        timeTableService.deleteEntry(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/timetable/class/{className}
    @GetMapping("/class/{className}")
    public ResponseEntity<List<TimeTableResponseDTO>> getByClass(@PathVariable String className) {
        return ResponseEntity.ok(
                timeTableService.getScheduleByClass(className).stream().map(this::toDTO).collect(Collectors.toList())
        );
    }

    // GET /api/timetable/class/{className}/day/{day}
    @GetMapping("/class/{className}/day/{day}")
    public ResponseEntity<List<TimeTableResponseDTO>> getByClassAndDay(
            @PathVariable String className, @PathVariable TimeTable.DayOfWeek day) {
        return ResponseEntity.ok(
                timeTableService.getScheduleByClassAndDay(className, day).stream().map(this::toDTO).collect(Collectors.toList())
        );
    }

    // GET /api/timetable/teacher/{teacherId}
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<TimeTableResponseDTO>> getByTeacher(@PathVariable Long teacherId) {
        return ResponseEntity.ok(
                timeTableService.getScheduleByTeacher(teacherId).stream().map(this::toDTO).collect(Collectors.toList())
        );
    }

    // GET /api/timetable/teacher/{teacherId}/day/{day}
    @GetMapping("/teacher/{teacherId}/day/{day}")
    public ResponseEntity<List<TimeTableResponseDTO>> getTeacherDaySchedule(
            @PathVariable Long teacherId, @PathVariable TimeTable.DayOfWeek day) {
        return ResponseEntity.ok(
                timeTableService.getTeacherScheduleForDay(teacherId, day).stream().map(this::toDTO).collect(Collectors.toList())
        );
    }
}