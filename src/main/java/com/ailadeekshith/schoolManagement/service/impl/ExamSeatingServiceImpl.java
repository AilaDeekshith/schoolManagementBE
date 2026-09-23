package com.ailadeekshith.schoolManagement.service.impl;

import com.ailadeekshith.schoolManagement.dto.ExamSeatDTO;
import com.ailadeekshith.schoolManagement.dto.ExamSeatingPlanDTO;
import com.ailadeekshith.schoolManagement.dto.ExamSeatingPlanRequest;
import com.ailadeekshith.schoolManagement.dto.ExamSessionDTO;
import com.ailadeekshith.schoolManagement.exception.ResourceNotFoundException;
import com.ailadeekshith.schoolManagement.model.Exam;
import com.ailadeekshith.schoolManagement.model.ExamSeat;
import com.ailadeekshith.schoolManagement.model.ExamSeatingPlan;
import com.ailadeekshith.schoolManagement.model.ExamSession;
import com.ailadeekshith.schoolManagement.model.Student;
import com.ailadeekshith.schoolManagement.model.Teacher;
import com.ailadeekshith.schoolManagement.repository.ExamRepository;
import com.ailadeekshith.schoolManagement.repository.ExamSeatRepository;
import com.ailadeekshith.schoolManagement.repository.ExamSeatingPlanRepository;
import com.ailadeekshith.schoolManagement.repository.ExamSessionRepository;
import com.ailadeekshith.schoolManagement.repository.StudentRepository;
import com.ailadeekshith.schoolManagement.repository.TeacherRepository;
import com.ailadeekshith.schoolManagement.service.ExamSeatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ExamSeatingServiceImpl implements ExamSeatingService {

    private final ExamSeatingPlanRepository planRepo;
    private final ExamSeatRepository seatRepo;
    private final ExamRepository examRepo;
    private final StudentRepository studentRepo;
    private final TeacherRepository teacherRepo;
    private final ExamSessionRepository sessionRepo;

    private Teacher invigilatorOf(Long id) {
        return id == null ? null : teacherRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found: " + id));
    }

    // ── Plans ────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<ExamSeatingPlanDTO> getPlansByExam(Long examId) {
        return planRepo.findByExamIdOrderByRoomNameAsc(examId)
                .stream().map(this::toPlanDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ExamSeatingPlanDTO getPlan(Long planId) {
        return toPlanDTO(loadPlan(planId));
    }

    @Override
    public ExamSeatingPlanDTO createPlan(ExamSeatingPlanRequest req) {
        Exam exam = examRepo.findById(req.getExamId())
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found: " + req.getExamId()));
        String room = req.getRoomName() == null ? "" : req.getRoomName().trim();
        if (room.isEmpty()) throw new IllegalArgumentException("Room name is required");
        // A room can host multiple sittings (different days/sessions), so no
        // room-uniqueness check here — a plan is one (room + date + session).
        ExamSeatingPlan plan = ExamSeatingPlan.builder()
                .exam(exam)
                .roomName(room)
                .rows(req.getRows())
                .columns(req.getColumns())
                .seatsPerBench(req.getSeatsPerBench() == null || req.getSeatsPerBench() < 1 ? 1 : req.getSeatsPerBench())
                .classNames(joinClasses(req.getClassNames()))
                .build();
        return toPlanDTO(planRepo.save(plan));
    }

    @Override
    public ExamSeatingPlanDTO updatePlan(Long planId, ExamSeatingPlanRequest req) {
        ExamSeatingPlan plan = loadPlan(planId);

        boolean layoutChanged =
                !equalsInt(plan.getRows(), req.getRows())
                || !equalsInt(plan.getColumns(), req.getColumns())
                || !equalsInt(plan.getSeatsPerBench(), req.getSeatsPerBench());

        if (req.getRoomName() != null && !req.getRoomName().trim().isEmpty()) {
            plan.setRoomName(req.getRoomName().trim());
        }
        plan.setRows(req.getRows());
        plan.setColumns(req.getColumns());
        plan.setSeatsPerBench(req.getSeatsPerBench() == null || req.getSeatsPerBench() < 1 ? 1 : req.getSeatsPerBench());
        plan.setClassNames(joinClasses(req.getClassNames()));

        // Resizing the room invalidates existing seat coordinates.
        if (layoutChanged) seatRepo.deleteByPlanId(planId);

        return toPlanDTO(planRepo.save(plan));
    }

    @Override
    public void deletePlan(Long planId) {
        ExamSeatingPlan plan = loadPlan(planId);
        seatRepo.deleteByPlanId(planId);
        sessionRepo.deleteByPlanId(planId);
        planRepo.delete(plan);
    }

    // ── Sessions ─────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<ExamSessionDTO> getSessions(Long planId) {
        return sessionRepo.findByPlanIdOrderByExamDateAscStartTimeAsc(planId)
                .stream().map(this::toSessionDTO).collect(Collectors.toList());
    }

    @Override
    public ExamSessionDTO addSession(Long planId, ExamSessionDTO dto) {
        ExamSeatingPlan plan = loadPlan(planId);
        ExamSession s = ExamSession.builder()
                .plan(plan)
                .examDate(dto.getExamDate())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .invigilator(invigilatorOf(dto.getInvigilatorId()))
                .build();
        return toSessionDTO(sessionRepo.save(s));
    }

    @Override
    public ExamSessionDTO updateSession(Long sessionId, ExamSessionDTO dto) {
        ExamSession s = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam session not found: " + sessionId));
        s.setExamDate(dto.getExamDate());
        s.setStartTime(dto.getStartTime());
        s.setEndTime(dto.getEndTime());
        s.setInvigilator(invigilatorOf(dto.getInvigilatorId()));
        return toSessionDTO(sessionRepo.save(s));
    }

    @Override
    public void deleteSession(Long sessionId) {
        ExamSession s = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam session not found: " + sessionId));
        sessionRepo.delete(s);
    }

    private ExamSessionDTO toSessionDTO(ExamSession s) {
        Teacher inv = s.getInvigilator();
        return ExamSessionDTO.builder()
                .id(s.getId())
                .planId(s.getPlan() != null ? s.getPlan().getId() : null)
                .examDate(s.getExamDate())
                .startTime(s.getStartTime())
                .endTime(s.getEndTime())
                .invigilatorId(inv != null ? inv.getId() : null)
                .invigilatorName(inv != null ? inv.getName() : null)
                .build();
    }

    // ── Seats ────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<ExamSeatDTO> getSeats(Long planId) {
        return seatRepo.findByPlanId(planId).stream().map(this::toSeatDTO).collect(Collectors.toList());
    }

    @Override
    public ExamSeatDTO assignStudent(Long planId, int rowNum, int colNum, int seatIndex, Long studentId) {
        ExamSeatingPlan plan = loadPlan(planId);
        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId));

        // If this student is already seated elsewhere in this plan, free that seat (move).
        seatRepo.findByPlanIdAndStudentId(planId, studentId).ifPresent(prev -> {
            boolean samePosition = prev.getRowNum() == rowNum && prev.getColNum() == colNum && prev.getSeatIndex() == seatIndex;
            if (!samePosition) {
                prev.setStudent(null);
                seatRepo.save(prev);
            }
        });

        Optional<ExamSeat> existing = seatRepo
                .findByPlanIdAndRowNumAndColNumAndSeatIndex(planId, rowNum, colNum, seatIndex);
        ExamSeat seat = existing.orElse(ExamSeat.builder()
                .plan(plan).rowNum(rowNum).colNum(colNum).seatIndex(seatIndex).build());
        seat.setStudent(student);
        return toSeatDTO(seatRepo.save(seat));
    }

    @Override
    public void unassignStudent(Long planId, int rowNum, int colNum, int seatIndex) {
        seatRepo.findByPlanIdAndRowNumAndColNumAndSeatIndex(planId, rowNum, colNum, seatIndex)
                .ifPresent(seatRepo::delete);
    }

    @Override
    public List<ExamSeatDTO> autoAssign(Long planId) {
        ExamSeatingPlan plan = loadPlan(planId);
        int rows = plan.getRows() == null ? 0 : plan.getRows();
        int cols = plan.getColumns() == null ? 0 : plan.getColumns();
        int perBench = plan.getSeatsPerBench() == null ? 1 : plan.getSeatsPerBench();

        List<ExamSeat> existingSeats = seatRepo.findByPlanId(planId);
        Set<String> occupiedPositions = new HashSet<>();
        Set<Long> seatedStudentIds = new HashSet<>();
        for (ExamSeat s : existingSeats) {
            if (s.getStudent() != null) {
                occupiedPositions.add(posKey(s.getRowNum(), s.getColNum(), s.getSeatIndex()));
                seatedStudentIds.add(s.getStudent().getId());
            }
        }

        // Build the ordered pool of students not yet seated.
        List<Student> pool = studentPool(plan).stream()
                .filter(st -> !seatedStudentIds.contains(st.getId()))
                .collect(Collectors.toList());

        int p = 0;
        for (int r = 0; r < rows && p < pool.size(); r++) {
            for (int c = 0; c < cols && p < pool.size(); c++) {
                for (int si = 0; si < perBench && p < pool.size(); si++) {
                    if (occupiedPositions.contains(posKey(r, c, si))) continue;
                    Student student = pool.get(p++);
                    Optional<ExamSeat> existing = seatRepo
                            .findByPlanIdAndRowNumAndColNumAndSeatIndex(planId, r, c, si);
                    ExamSeat seat = existing.orElse(ExamSeat.builder()
                            .plan(plan).rowNum(r).colNum(c).seatIndex(si).build());
                    seat.setStudent(student);
                    seatRepo.save(seat);
                }
            }
        }
        return getSeats(planId);
    }

    @Override
    public void clearSeats(Long planId) {
        loadPlan(planId);
        seatRepo.deleteByPlanId(planId);
    }

    // ── Helpers ──────────────────────────────────────────────
    private List<Student> studentPool(ExamSeatingPlan plan) {
        List<String> classes = splitClasses(plan.getClassNames());
        // LinkedHashSet keeps class order while de-duplicating students.
        Set<Student> pool = new LinkedHashSet<>();
        for (String cls : classes) {
            List<Student> inClass = new ArrayList<>(studentRepo.findByClassName(cls));
            inClass.sort(Comparator.comparing(s -> s.getRollNumber() == null ? Integer.MAX_VALUE : s.getRollNumber()));
            pool.addAll(inClass);
        }
        return new ArrayList<>(pool);
    }

    private ExamSeatingPlan loadPlan(Long planId) {
        return planRepo.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Seating plan not found: " + planId));
    }

    private String posKey(int r, int c, int s) { return r + ":" + c + ":" + s; }

    private boolean equalsInt(Integer a, Integer b) {
        if (a == null && b == null) return true;
        return a != null && a.equals(b);
    }

    private String joinClasses(List<String> classes) {
        if (classes == null || classes.isEmpty()) return null;
        return classes.stream().filter(c -> c != null && !c.trim().isEmpty())
                .map(String::trim).distinct().collect(Collectors.joining(","));
    }

    private List<String> splitClasses(String csv) {
        if (csv == null || csv.trim().isEmpty()) return new ArrayList<>();
        List<String> out = new ArrayList<>();
        for (String s : csv.split(",")) {
            String t = s.trim();
            if (!t.isEmpty()) out.add(t);
        }
        return out;
    }

    private ExamSeatingPlanDTO toPlanDTO(ExamSeatingPlan plan) {
        int rows = plan.getRows() == null ? 0 : plan.getRows();
        int cols = plan.getColumns() == null ? 0 : plan.getColumns();
        int perBench = plan.getSeatsPerBench() == null ? 1 : plan.getSeatsPerBench();
        int total = rows * cols * perBench;
        int seated = (int) seatRepo.findByPlanId(plan.getId()).stream()
                .filter(s -> s.getStudent() != null).count();
        Exam exam = plan.getExam();
        return ExamSeatingPlanDTO.builder()
                .id(plan.getId())
                .examId(exam != null ? exam.getId() : null)
                .examName(exam != null ? exam.getName() : null)
                .examDate(exam != null ? exam.getExamDate() : null)
                .roomName(plan.getRoomName())
                .sessions(sessionRepo.findByPlanIdOrderByExamDateAscStartTimeAsc(plan.getId())
                        .stream().map(this::toSessionDTO).collect(Collectors.toList()))
                .rows(plan.getRows())
                .columns(plan.getColumns())
                .seatsPerBench(plan.getSeatsPerBench())
                .classNames(splitClasses(plan.getClassNames()))
                .totalSeats(total)
                .seatedCount(seated)
                .build();
    }

    private ExamSeatDTO toSeatDTO(ExamSeat seat) {
        Student st = seat.getStudent();
        return ExamSeatDTO.builder()
                .id(seat.getId())
                .rowNum(seat.getRowNum())
                .colNum(seat.getColNum())
                .seatIndex(seat.getSeatIndex())
                .studentId(st != null ? st.getId() : null)
                .studentName(st != null ? st.getName() : null)
                .studentClass(st != null ? st.getClassName() : null)
                .rollNumber(st != null ? st.getRollNumber() : null)
                .studentPhoto(st != null ? st.getPhotoBase64() : null)
                .build();
    }
}
