package com.ailadeekshith.schoolManagement.controller;

import com.ailadeekshith.schoolManagement.model.*;
import com.ailadeekshith.schoolManagement.repository.*;
import com.ailadeekshith.schoolManagement.service.ClassDiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StudentPortalController {

    private final ExamResultRepository examResultRepo;
    private final AttendanceRepository attendanceRepo;
    private final TimeTableRepository timetableRepo;
    private final ExamRepository examRepo;
    private final FeesRepository feesRepo;
    private final ClassDiaryService classDiaryService;

    private StudentUser getStudentUser(Authentication auth) {
        return (StudentUser) auth.getPrincipal();
    }

    @GetMapping("/profile")
    public ResponseEntity<Student> getProfile(Authentication auth) {
        return ResponseEntity.ok(getStudentUser(auth).getStudent());
    }

    @GetMapping("/marks")
    public ResponseEntity<List<ExamResult>> getMarks(Authentication auth) {
        Long studentId = getStudentUser(auth).getStudent().getId();
        return ResponseEntity.ok(examResultRepo.findByStudentId(studentId));
    }

    @GetMapping("/attendance")
    public ResponseEntity<List<Attendance>> getAttendance(
            Authentication auth,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        Long studentId = getStudentUser(auth).getStudent().getId();
        LocalDate fromDate = from != null ? LocalDate.parse(from) : LocalDate.now().minusDays(30);
        LocalDate toDate   = to   != null ? LocalDate.parse(to)   : LocalDate.now();
        return ResponseEntity.ok(attendanceRepo.findByStudentIdAndDateBetween(studentId, fromDate, toDate));
    }

    @GetMapping("/timetable/today")
    public ResponseEntity<List<TimeTable>> getTodayTimetable(Authentication auth) {
        String className = getStudentUser(auth).getStudent().getClassName();
        String dayName   = LocalDate.now().getDayOfWeek().name();
        try {
            TimeTable.DayOfWeek day = TimeTable.DayOfWeek.valueOf(dayName);
            List<TimeTable> schedule = timetableRepo.findByClassNameAndDayOfWeek(className, day);
            schedule.sort((a, b) -> a.getPeriodNumber().compareTo(b.getPeriodNumber()));
            return ResponseEntity.ok(schedule);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(List.of());
        }
    }

    @GetMapping("/exams/upcoming")
    public ResponseEntity<List<Exam>> getUpcomingExams(Authentication auth) {
        String className = getStudentUser(auth).getStudent().getClassName();
        List<Exam> upcoming = examRepo.findUpcomingExamsByClass(className)
                .stream()
                .filter(e -> !e.getExamDate().isBefore(LocalDate.now()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(upcoming);
    }

    @GetMapping("/fees")
    public ResponseEntity<List<Fees>> getFees(Authentication auth) {
        Long studentId = getStudentUser(auth).getStudent().getId();
        return ResponseEntity.ok(feesRepo.findByStudentId(studentId));
    }

    /** Class diary entries for the student's own class — a specific date, or a range (defaults to the last 7 days). */
    @GetMapping("/class-diary")
    public ResponseEntity<List<ClassDiaryEntry>> getClassDiary(
            Authentication auth,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        String className = getStudentUser(auth).getStudent().getClassName();
        if (date != null) {
            return ResponseEntity.ok(classDiaryService.getByClassAndDate(className, LocalDate.parse(date)));
        }
        LocalDate fromDate = from != null ? LocalDate.parse(from) : LocalDate.now().minusDays(7);
        LocalDate toDate   = to   != null ? LocalDate.parse(to)   : LocalDate.now();
        return ResponseEntity.ok(classDiaryService.getByClassAndDateRange(className, fromDate, toDate));
    }
}
