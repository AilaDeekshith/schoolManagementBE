package com.ailadeekshith.schoolManagement.controller;

import com.ailadeekshith.schoolManagement.model.ClassDiaryEntry;
import com.ailadeekshith.schoolManagement.model.Teacher;
import com.ailadeekshith.schoolManagement.repository.TeacherRepository;
import com.ailadeekshith.schoolManagement.service.ClassDiaryService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/** Admin/web-facing class diary endpoints — full visibility and the ability to enter/edit on a teacher's behalf. */
@RestController
@RequestMapping("/api/class-diary")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ClassDiaryController {

    private final ClassDiaryService diaryService;
    private final TeacherRepository teacherRepo;

    /** GET /api/class-diary?className=X&date=Y  — or  ?className=X&from=Y&to=Z (defaults to the last 30 days). */
    @GetMapping
    public ResponseEntity<List<ClassDiaryEntry>> getEntries(
            @RequestParam String className,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        if (date != null) {
            return ResponseEntity.ok(diaryService.getByClassAndDate(className, date));
        }
        LocalDate f = from != null ? from : LocalDate.now().minusDays(30);
        LocalDate t = to != null ? to : LocalDate.now();
        return ResponseEntity.ok(diaryService.getByClassAndDateRange(className, f, t));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassDiaryEntry> getById(@PathVariable Long id) {
        return ResponseEntity.ok(diaryService.getById(id));
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<ClassDiaryEntry>> getByTeacher(@PathVariable Long teacherId) {
        return ResponseEntity.ok(diaryService.getByTeacher(teacherId));
    }

    /** PUT /api/class-diary/timetable/{timetableId}?date=Y — creates or updates that slot's entry for the date. */
    @PutMapping("/timetable/{timetableId}")
    public ResponseEntity<ClassDiaryEntry> save(
            @PathVariable Long timetableId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestBody ClassDiarySaveRequest req) {
        Teacher teacher = req.getTeacherId() != null ? teacherRepo.findById(req.getTeacherId()).orElse(null) : null;
        ClassDiaryEntry data = ClassDiaryEntry.builder()
                .topicCovered(req.getTopicCovered())
                .subTopics(req.getSubTopics())
                .homework(req.getHomework())
                .instructionsForNext(req.getInstructionsForNext())
                .build();
        return ResponseEntity.ok(diaryService.save(timetableId, date, teacher, data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        diaryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Data
    public static class ClassDiarySaveRequest {
        private Long teacherId;
        private String topicCovered;
        private String subTopics;
        private String homework;
        private String instructionsForNext;
    }
}
