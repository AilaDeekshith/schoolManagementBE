package com.ailadeekshith.schoolManagement.controller;

import com.ailadeekshith.schoolManagement.dto.ExamSeatDTO;
import com.ailadeekshith.schoolManagement.dto.ExamSeatingPlanDTO;
import com.ailadeekshith.schoolManagement.dto.LayoutUpdateDTO;
import com.ailadeekshith.schoolManagement.dto.SeatAssignmentDTO;
import com.ailadeekshith.schoolManagement.exception.BadRequestException;
import com.ailadeekshith.schoolManagement.model.*;
import com.ailadeekshith.schoolManagement.repository.*;
import com.ailadeekshith.schoolManagement.service.ClassDiaryService;
import com.ailadeekshith.schoolManagement.service.ExamSeatingService;
import com.ailadeekshith.schoolManagement.service.ReferenceResolver;
import com.ailadeekshith.schoolManagement.service.SeatAssignmentService;
import com.ailadeekshith.schoolManagement.service.StudentService;
import com.ailadeekshith.schoolManagement.service.SyllabusService;
import com.ailadeekshith.schoolManagement.service.TimeTableService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mobile app entry point for the TEACHER role. Every endpoint here is scoped to
 * the calling teacher's own assigned classes ({@link Teacher#getAssignedClasses()}
 * — the same comma-separated list maintained via the web app's Add/Edit Teacher
 * form) so a teacher can never read or write data for a class they aren't
 * assigned to.
 */
@RestController
@RequestMapping("/api/teacher-portal")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TeacherPortalController {

    private final AppUserRepository appUserRepo;
    private final StudentRepository studentRepo;
    private final StudentService studentService;
    private final AttendanceRepository attendanceRepo;
    private final ExamRepository examRepo;
    private final ExamResultRepository resultRepo;
    private final SyllabusService syllabusService;
    private final SyllabusTopicRepository topicRepo;
    private final TimeTableService timeTableService;
    private final ReferenceResolver referenceResolver;
    private final ClassRoomRepository classRoomRepo;
    private final SeatAssignmentService seatAssignmentService;
    private final ExamSeatingService examSeatingService;
    private final ClassDiaryService classDiaryService;

    // ── Access helpers ───────────────────────────────────────────────

    private Teacher getTeacher(Authentication auth) {
        AppUser user = appUserRepo.findByUsername(auth.getName())
                .orElseThrow(() -> new BadRequestException("User not found"));
        if (user.getRole() != AppUser.UserRole.TEACHER || user.getTeacher() == null) {
            throw new AccessDeniedException("This account is not linked to a teacher profile");
        }
        return user.getTeacher();
    }

    private Set<String> assignedClasses(Teacher teacher) {
        if (teacher.getAssignedClasses() == null || teacher.getAssignedClasses().isBlank()) {
            return Set.of();
        }
        return Arrays.stream(teacher.getAssignedClasses().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private void requireClassAccess(Teacher teacher, String className) {
        if (!assignedClasses(teacher).contains(className.trim())) {
            throw new AccessDeniedException("You are not assigned to class " + className);
        }
    }

    /** True if any of the exam's classes is one the teacher is assigned to. */
    private void requireExamAccess(Teacher teacher, Exam exam) {
        Set<String> assigned = assignedClasses(teacher);
        boolean allowed = exam.getClasses() != null && exam.getClasses().stream().anyMatch(assigned::contains);
        if (!allowed) throw new AccessDeniedException("You are not assigned to any class for this exam");
    }

    // ── Profile ──────────────────────────────────────────────────────

    @GetMapping("/me")
    public ResponseEntity<Teacher> me(Authentication auth) {
        return ResponseEntity.ok(getTeacher(auth));
    }

    // ── Classes / roster ─────────────────────────────────────────────

    @GetMapping("/classes")
    public ResponseEntity<List<ClassSummaryDTO>> getClasses(Authentication auth) {
        Teacher teacher = getTeacher(auth);
        List<ClassSummaryDTO> out = new ArrayList<>();
        for (String className : assignedClasses(teacher)) {
            out.add(new ClassSummaryDTO(className, studentRepo.countByClassName(className)));
        }
        return ResponseEntity.ok(out);
    }

    @GetMapping("/students")
    public ResponseEntity<List<Student>> getStudents(Authentication auth, @RequestParam String className) {
        Teacher teacher = getTeacher(auth);
        requireClassAccess(teacher, className);
        return ResponseEntity.ok(studentService.getStudentsByClass(className));
    }

    // ── Attendance ───────────────────────────────────────────────────

    @GetMapping("/attendance")
    public ResponseEntity<List<Attendance>> getAttendance(
            Authentication auth,
            @RequestParam String className,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Teacher teacher = getTeacher(auth);
        requireClassAccess(teacher, className);
        return ResponseEntity.ok(attendanceRepo.findByClassNameAndDate(className, date));
    }

    @PostMapping("/attendance")
    public ResponseEntity<?> saveAttendance(Authentication auth, @RequestBody BulkAttendanceRequest req) {
        Teacher teacher = getTeacher(auth);
        requireClassAccess(teacher, req.getClassName());

        for (AttendanceEntry entry : req.getRecords()) {
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
            record.setMarkedBy(teacher.getName());
            if (record.getSection() == null) record.setSection(referenceResolver.resolveSection(req.getClassName()));
            attendanceRepo.save(record);
        }
        return ResponseEntity.ok(Map.of("message", "Attendance saved"));
    }

    // ── Exams ────────────────────────────────────────────────────────

    @GetMapping("/exams")
    public ResponseEntity<List<Exam>> getExams(Authentication auth, @RequestParam(required = false) String className) {
        Teacher teacher = getTeacher(auth);
        if (className != null && !className.isBlank()) {
            requireClassAccess(teacher, className);
            return ResponseEntity.ok(examRepo.findByClassName(className));
        }
        Set<String> assigned = assignedClasses(teacher);
        List<Exam> combined = new ArrayList<>();
        Set<Long> seen = new LinkedHashSet<>();
        for (String cn : assigned) {
            for (Exam e : examRepo.findByClassName(cn)) {
                if (seen.add(e.getId())) combined.add(e);
            }
        }
        return ResponseEntity.ok(combined);
    }

    @GetMapping("/exam-results")
    public ResponseEntity<List<ExamResult>> getExamResults(Authentication auth, @RequestParam Long examId) {
        Teacher teacher = getTeacher(auth);
        Exam exam = examRepo.findById(examId).orElseThrow(() -> new BadRequestException("Exam not found"));
        requireExamAccess(teacher, exam);
        return ResponseEntity.ok(resultRepo.findByExamId(examId));
    }

    @PostMapping("/exam-results/bulk")
    public ResponseEntity<List<ExamResult>> saveExamResults(Authentication auth, @RequestBody ExamResultBulkRequest req) {
        Teacher teacher = getTeacher(auth);
        Exam exam = examRepo.findById(req.getExamId()).orElseThrow(() -> new BadRequestException("Exam not found"));
        requireExamAccess(teacher, exam);
        validateMarks(req.getEntries(), exam.getMaxMarks());

        List<ExamResult> saved = new ArrayList<>();
        for (ExamResultEntry entry : req.getEntries()) {
            Student student = studentRepo.findById(entry.getStudentId()).orElse(null);
            if (student == null) continue;

            ExamResult result = resultRepo
                    .findByExamIdAndStudentIdAndSubject(req.getExamId(), entry.getStudentId(), req.getSubject())
                    .orElseGet(() -> ExamResult.builder()
                            .exam(exam).student(student).subject(req.getSubject()).build());

            Integer maxMarks = exam.getMaxMarks();
            result.setMarksObtained(entry.getMarksObtained());
            result.setMaxMarks(maxMarks);
            result.setGrade(entry.getMarksObtained() != null && maxMarks != null
                    ? grade(entry.getMarksObtained(), maxMarks) : null);
            result.setRemarks(entry.getRemarks());
            if (result.getSubjectRef() == null) result.setSubjectRef(referenceResolver.resolveSubject(req.getSubject()));
            saved.add(resultRepo.save(result));
        }
        return ResponseEntity.ok(saved);
    }

    // Rejects the whole batch (before anything is saved) if any entry falls
    // outside [0, maxMarks] — maxMarks is optional on an exam, so entries are
    // only bounds-checked when the exam actually defines one.
    private static void validateMarks(List<ExamResultEntry> entries, Integer maxMarks) {
        for (ExamResultEntry entry : entries) {
            Double marks = entry.getMarksObtained();
            if (marks == null) continue;
            if (marks < 0) {
                throw new BadRequestException("Marks cannot be negative");
            }
            if (maxMarks != null && marks > maxMarks) {
                throw new BadRequestException("Marks cannot exceed the exam maximum of " + maxMarks);
            }
        }
    }

    private static String grade(double marks, int max) {
        if (max == 0) return "—";
        double pct = (marks / max) * 100;
        if (pct >= 90) return "A+";
        if (pct >= 80) return "A";
        if (pct >= 70) return "B";
        if (pct >= 60) return "C";
        if (pct >= 50) return "D";
        return "F";
    }

    // ── Syllabus ─────────────────────────────────────────────────────

    @GetMapping("/syllabus")
    public ResponseEntity<List<Syllabus>> getSyllabus(Authentication auth, @RequestParam String className) {
        Teacher teacher = getTeacher(auth);
        requireClassAccess(teacher, className);
        Section section = referenceResolver.resolveSection(className);
        String gradeName = section != null ? section.getGrade().getName() : null;
        if (gradeName == null) return ResponseEntity.ok(List.of());
        return ResponseEntity.ok(syllabusService.getByGrade(gradeName));
    }

    /** Creates a new subject syllabus for one of the teacher's assigned classes, in their own subject. */
    @PostMapping("/syllabus")
    public ResponseEntity<Syllabus> createSyllabus(Authentication auth, @RequestBody SyllabusCreateRequest req) {
        Teacher teacher = getTeacher(auth);
        requireClassAccess(teacher, req.getClassName());
        Section section = referenceResolver.resolveSection(req.getClassName());
        if (section == null) throw new BadRequestException("Could not resolve a grade for " + req.getClassName());

        Syllabus syllabus = Syllabus.builder()
                .gradeName(section.getGrade().getName())
                .subjectName(teacher.getSubject())
                .academicYear(req.getAcademicYear())
                .description(req.getDescription())
                .totalHours(req.getTotalHours())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(syllabusService.create(syllabus));
    }

    /** Adds a topic to a syllabus the teacher owns (their subject, one of their assigned grades). */
    @PostMapping("/syllabus/{syllabusId}/topics")
    public ResponseEntity<SyllabusTopic> addTopic(
            Authentication auth, @PathVariable Long syllabusId, @RequestBody TopicCreateRequest req) {
        Teacher teacher = getTeacher(auth);
        Syllabus syllabus = syllabusService.getById(syllabusId);
        requireSyllabusAccess(teacher, syllabus);

        SyllabusTopic topic = SyllabusTopic.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .estimatedHours(req.getEstimatedHours())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(syllabusService.addTopic(syllabusId, topic));
    }

    /** Adds a reference (link/video/doc) to a topic the teacher owns. */
    @PostMapping("/syllabus/topics/{topicId}/references")
    public ResponseEntity<TopicReference> addReference(
            Authentication auth, @PathVariable Long topicId, @RequestBody ReferenceCreateRequest req) {
        Teacher teacher = getTeacher(auth);
        requireTopicAccess(teacher, topicId);

        TopicReference reference = TopicReference.builder()
                .title(req.getTitle())
                .url(req.getUrl())
                .type(req.getType())
                .description(req.getDescription())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(syllabusService.addReference(topicId, reference));
    }

    @PatchMapping("/syllabus/topics/{topicId}/status")
    public ResponseEntity<SyllabusTopic> patchTopicStatus(
            Authentication auth, @PathVariable Long topicId, @RequestParam SyllabusTopic.TopicStatus status) {
        Teacher teacher = getTeacher(auth);
        requireTopicAccess(teacher, topicId);
        return ResponseEntity.ok(syllabusService.patchStatus(topicId, status));
    }

    @PatchMapping("/syllabus/topics/{topicId}/key")
    public ResponseEntity<SyllabusTopic> patchTopicKey(
            Authentication auth, @PathVariable Long topicId, @RequestParam boolean flag) {
        Teacher teacher = getTeacher(auth);
        requireTopicAccess(teacher, topicId);
        return ResponseEntity.ok(syllabusService.patchKeyTopic(topicId, flag));
    }

    /** A teacher may only touch syllabi for their own subject, in one of their assigned grades. */
    private void requireSyllabusAccess(Teacher teacher, Syllabus syllabus) {
        if (!syllabus.getSubjectName().equalsIgnoreCase(teacher.getSubject())) {
            throw new AccessDeniedException("You do not teach this subject");
        }
        boolean gradeAssigned = assignedClasses(teacher).stream()
                .anyMatch(cn -> {
                    Section s = referenceResolver.resolveSection(cn);
                    return s != null && s.getGrade().getName().equalsIgnoreCase(syllabus.getGradeName());
                });
        if (!gradeAssigned) throw new AccessDeniedException("You are not assigned to this grade");
    }

    private void requireTopicAccess(Teacher teacher, Long topicId) {
        SyllabusTopic topic = topicRepo.findById(topicId)
                .orElseThrow(() -> new BadRequestException("Topic not found: " + topicId));
        requireSyllabusAccess(teacher, topic.getSyllabus());
    }

    // ── Timetable ────────────────────────────────────────────────────

    /** The teacher's own weekly schedule, across all their classes. */
    @GetMapping("/timetable")
    public ResponseEntity<List<TimetableEntryDTO>> getTimetable(Authentication auth) {
        Teacher teacher = getTeacher(auth);
        return ResponseEntity.ok(toTimetableDTOs(timeTableService.getScheduleByTeacher(teacher.getId())));
    }

    /** The full weekly schedule for one assigned class — every subject/teacher, not just this teacher's periods. */
    @GetMapping("/timetable/class")
    public ResponseEntity<List<TimetableEntryDTO>> getClassTimetable(Authentication auth, @RequestParam String className) {
        Teacher teacher = getTeacher(auth);
        requireClassAccess(teacher, className);
        return ResponseEntity.ok(toTimetableDTOs(timeTableService.getScheduleByClass(className)));
    }

    private List<TimetableEntryDTO> toTimetableDTOs(List<TimeTable> entries) {
        return entries.stream()
                .map(t -> new TimetableEntryDTO(
                        t.getId(), t.getDayOfWeek(), t.getPeriodNumber(), t.getSubject(),
                        t.getStartTime() != null ? t.getStartTime().toString() : null,
                        t.getEndTime() != null ? t.getEndTime().toString() : null,
                        t.getClassName(),
                        t.getTeacher() != null ? t.getTeacher().getName() : null))
                .collect(Collectors.toList());
    }

    // ── Class diary (end-of-period summary) ───────────────────────────

    /** Entries for one assigned class — a specific date, or a range (defaults to the last 30 days). */
    @GetMapping("/class-diary")
    public ResponseEntity<List<ClassDiaryEntry>> getClassDiary(
            Authentication auth, @RequestParam String className,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        Teacher teacher = getTeacher(auth);
        requireClassAccess(teacher, className);
        if (date != null) {
            return ResponseEntity.ok(classDiaryService.getByClassAndDate(className, date));
        }
        LocalDate f = from != null ? from : LocalDate.now().minusDays(30);
        LocalDate t = to != null ? to : LocalDate.now();
        return ResponseEntity.ok(classDiaryService.getByClassAndDateRange(className, f, t));
    }

    /** Every entry this teacher has personally logged, most recent first. */
    @GetMapping("/class-diary/mine")
    public ResponseEntity<List<ClassDiaryEntry>> getMyClassDiary(Authentication auth) {
        Teacher teacher = getTeacher(auth);
        return ResponseEntity.ok(classDiaryService.getByTeacher(teacher.getId()));
    }

    /** Log (or edit) the summary for one of the teacher's own periods on a given date. */
    @PutMapping("/class-diary/timetable/{timetableId}")
    public ResponseEntity<ClassDiaryEntry> saveClassDiary(
            Authentication auth, @PathVariable Long timetableId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestBody ClassDiarySaveRequest req) {
        Teacher teacher = getTeacher(auth);
        requireTimetableOwnership(teacher, timetableId);

        ClassDiaryEntry data = ClassDiaryEntry.builder()
                .topicCovered(req.getTopicCovered())
                .subTopics(req.getSubTopics())
                .homework(req.getHomework())
                .instructionsForNext(req.getInstructionsForNext())
                .build();
        return ResponseEntity.ok(classDiaryService.save(timetableId, date, teacher, data));
    }

    /** A teacher may only log entries for their own periods, in one of their assigned classes. */
    private void requireTimetableOwnership(Teacher teacher, Long timetableId) {
        TimeTable slot = timeTableService.getEntryById(timetableId);
        requireClassAccess(teacher, slot.getClassName());
        if (slot.getTeacher() == null || !slot.getTeacher().getId().equals(teacher.getId())) {
            throw new AccessDeniedException("This period isn't assigned to you");
        }
    }

    // ── Classroom seating ────────────────────────────────────────────

    private ClassRoom getClassRoom(String className) {
        return classRoomRepo.findByClassName(className)
                .orElseThrow(() -> new BadRequestException("No classroom is configured for " + className));
    }

    @GetMapping("/classroom")
    public ResponseEntity<ClassroomLayoutDTO> getClassroom(Authentication auth, @RequestParam String className) {
        Teacher teacher = getTeacher(auth);
        requireClassAccess(teacher, className);
        ClassRoom room = getClassRoom(className);
        List<SeatAssignmentDTO> seats = seatAssignmentService.getSeatsForClass(room.getId());
        return ResponseEntity.ok(new ClassroomLayoutDTO(
                room.getId(), room.getRows(), room.getColumns(), room.getStudentsPerBench(), seats));
    }

    @PutMapping("/classroom/layout")
    public ResponseEntity<ClassroomLayoutDTO> updateClassroomLayout(
            Authentication auth, @RequestParam String className, @RequestBody LayoutUpdateDTO dto) {
        Teacher teacher = getTeacher(auth);
        requireClassAccess(teacher, className);
        ClassRoom room = getClassRoom(className);
        ClassRoom updated = seatAssignmentService.updateLayout(room.getId(), dto.getRows(), dto.getColumns(), dto.getStudentsPerBench());
        return ResponseEntity.ok(new ClassroomLayoutDTO(
                updated.getId(), updated.getRows(), updated.getColumns(), updated.getStudentsPerBench(), List.of()));
    }

    @PutMapping("/classroom/seats/{row}/{col}/{seat}")
    public ResponseEntity<SeatAssignmentDTO> assignClassroomSeat(
            Authentication auth, @RequestParam String className,
            @PathVariable int row, @PathVariable int col, @PathVariable int seat,
            @RequestBody SeatAssignRequestBody body) {
        Teacher teacher = getTeacher(auth);
        requireClassAccess(teacher, className);
        ClassRoom room = getClassRoom(className);
        return ResponseEntity.ok(seatAssignmentService.assignStudent(room.getId(), row, col, seat, body.getStudentId()));
    }

    @DeleteMapping("/classroom/seats/{row}/{col}/{seat}")
    public ResponseEntity<Void> unassignClassroomSeat(
            Authentication auth, @RequestParam String className,
            @PathVariable int row, @PathVariable int col, @PathVariable int seat) {
        Teacher teacher = getTeacher(auth);
        requireClassAccess(teacher, className);
        ClassRoom room = getClassRoom(className);
        seatAssignmentService.unassignStudent(room.getId(), row, col, seat);
        return ResponseEntity.noContent().build();
    }

    // ── Exam seating (read-only — room/plan setup stays an admin function) ──

    @GetMapping("/exam-seating")
    public ResponseEntity<List<ExamSeatingPlanDTO>> getExamSeatingPlans(Authentication auth, @RequestParam Long examId) {
        Teacher teacher = getTeacher(auth);
        Exam exam = examRepo.findById(examId).orElseThrow(() -> new BadRequestException("Exam not found"));
        requireExamAccess(teacher, exam);
        return ResponseEntity.ok(examSeatingService.getPlansByExam(examId));
    }

    @GetMapping("/exam-seating/{planId}/seats")
    public ResponseEntity<List<ExamSeatDTO>> getExamSeats(Authentication auth, @PathVariable Long planId) {
        Teacher teacher = getTeacher(auth);
        ExamSeatingPlanDTO plan = examSeatingService.getPlan(planId);
        Exam exam = examRepo.findById(plan.getExamId()).orElseThrow(() -> new BadRequestException("Exam not found"));
        requireExamAccess(teacher, exam);
        return ResponseEntity.ok(examSeatingService.getSeats(planId));
    }

    // ── DTOs / request bodies ────────────────────────────────────────

    public record ClassSummaryDTO(String className, long studentCount) {}

    public record TimetableEntryDTO(
            Long id, TimeTable.DayOfWeek dayOfWeek, Integer periodNumber, String subject,
            String startTime, String endTime, String className, String teacherName) {}

    public record ClassroomLayoutDTO(
            Long classRoomId, Integer rows, Integer columns, Integer studentsPerBench,
            List<SeatAssignmentDTO> seats) {}

    @Data
    public static class SeatAssignRequestBody {
        private Long studentId;
    }

    @Data
    public static class ClassDiarySaveRequest {
        private String topicCovered;
        private String subTopics;
        private String homework;
        private String instructionsForNext;
    }

    @Data
    public static class SyllabusCreateRequest {
        private String className;
        private String academicYear;
        private String description;
        private Integer totalHours;
    }

    @Data
    public static class TopicCreateRequest {
        private String title;
        private String description;
        private Integer estimatedHours;
    }

    @Data
    public static class ReferenceCreateRequest {
        private String title;
        private String url;
        private TopicReference.ReferenceType type;
        private String description;
    }

    @Data
    public static class BulkAttendanceRequest {
        private String className;
        private LocalDate date;
        private List<AttendanceEntry> records;
    }

    @Data
    public static class AttendanceEntry {
        private Long studentId;
        private Attendance.AttendanceStatus status;
        private String remarks;
    }

    @Data
    public static class ExamResultBulkRequest {
        private Long examId;
        private String subject;
        private List<ExamResultEntry> entries;
    }

    @Data
    public static class ExamResultEntry {
        private Long studentId;
        private Double marksObtained;
        private String remarks;
    }
}
