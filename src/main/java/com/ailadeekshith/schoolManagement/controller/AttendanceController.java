package com.ailadeekshith.schoolManagement.controller;

import com.ailadeekshith.schoolManagement.model.Attendance;
import com.ailadeekshith.schoolManagement.model.Student;
import com.ailadeekshith.schoolManagement.repository.AttendanceRepository;
import com.ailadeekshith.schoolManagement.repository.StudentRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AttendanceController {

    private final AttendanceRepository attendanceRepo;
    private final StudentRepository studentRepo;

    @PostMapping
    public ResponseEntity<?> saveAttendance(@RequestBody BulkAttendanceRequest req,
                                            Authentication auth) {
        String markedBy = auth != null ? auth.getName() : "system";
        for (RecordEntry entry : req.getRecords()) {
            Student student = studentRepo.findById(entry.getStudentId()).orElse(null);
            if (student == null) continue;

            Attendance record = attendanceRepo
                    .findByStudentIdAndClassNameAndDate(entry.getStudentId(), req.getClassName(), req.getDate())
                    .orElseGet(() -> Attendance.builder()
                            .student(student)
                            .className(req.getClassName())
                            .date(req.getDate())
                            .build());

            record.setStatus(entry.getStatus());
            record.setRemarks(entry.getRemarks());
            record.setMarkedBy(markedBy);
            attendanceRepo.save(record);
        }
        return ResponseEntity.ok(Map.of("message", "Attendance saved"));
    }

    @GetMapping("/class/{className}/date/{date}")
    public ResponseEntity<List<Attendance>> getByClassAndDate(
            @PathVariable String className,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(attendanceRepo.findByClassNameAndDate(className, date));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Attendance>> getByStudent(
            @PathVariable Long studentId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        LocalDate start = from != null ? from : LocalDate.now().minusMonths(1);
        LocalDate end = to != null ? to : LocalDate.now();
        return ResponseEntity.ok(attendanceRepo.findByStudentIdAndDateBetween(studentId, start, end));
    }

    @GetMapping("/summary/today")
    public ResponseEntity<Map<String, Object>> getTodaySummary() {
        LocalDate today = LocalDate.now();
        long total   = attendanceRepo.findByDate(today).size();
        long present = attendanceRepo.countByDateAndStatus(today, Attendance.AttendanceStatus.PRESENT);
        long absent  = attendanceRepo.countByDateAndStatus(today, Attendance.AttendanceStatus.ABSENT);
        double pct   = total > 0 ? Math.round((present * 100.0 / total) * 10) / 10.0 : 0.0;
        return ResponseEntity.ok(Map.of(
            "date",            today.toString(),
            "total",           total,
            "present",         present,
            "absent",          absent,
            "percentage",      pct,
            "hasData",         total > 0
        ));
    }

    @GetMapping("/class/{className}/summary")
    public ResponseEntity<List<Attendance>> getClassSummary(
            @PathVariable String className,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(attendanceRepo.findByClassNameAndDateBetween(className, from, to));
    }

    @Data
    public static class BulkAttendanceRequest {
        private String className;
        private LocalDate date;
        private List<RecordEntry> records;
    }

    @Data
    public static class RecordEntry {
        private Long studentId;
        private Attendance.AttendanceStatus status;
        private String remarks;
    }
}
