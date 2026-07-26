package com.ailadeekshith.schoolManagement.service.impl;

import com.ailadeekshith.schoolManagement.dto.ExamScheduleDTO;
import com.ailadeekshith.schoolManagement.exception.ResourceNotFoundException;
import com.ailadeekshith.schoolManagement.model.Exam;
import com.ailadeekshith.schoolManagement.model.ExamSchedule;
import com.ailadeekshith.schoolManagement.repository.ExamRepository;
import com.ailadeekshith.schoolManagement.repository.ExamScheduleRepository;
import com.ailadeekshith.schoolManagement.service.ExamScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ExamScheduleServiceImpl implements ExamScheduleService {

    private final ExamScheduleRepository scheduleRepo;
    private final ExamRepository examRepo;

    @Override
    @Transactional(readOnly = true)
    public List<ExamScheduleDTO> getByExam(Long examId) {
        return scheduleRepo.findByExamIdOrderByExamDateAscStartTimeAsc(examId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public ExamScheduleDTO create(Long examId, ExamScheduleDTO dto) {
        Exam exam = examRepo.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found: " + examId));
        ExamSchedule s = ExamSchedule.builder()
                .exam(exam)
                .subject(dto.getSubject())
                .examDate(dto.getExamDate())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .className(dto.getClassName())
                .maxMarks(dto.getMaxMarks())
                .room(dto.getRoom())
                .notes(dto.getNotes())
                .build();
        return toDTO(scheduleRepo.save(s));
    }

    @Override
    public ExamScheduleDTO update(Long id, ExamScheduleDTO dto) {
        ExamSchedule s = scheduleRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam schedule not found: " + id));
        s.setSubject(dto.getSubject());
        s.setExamDate(dto.getExamDate());
        s.setStartTime(dto.getStartTime());
        s.setEndTime(dto.getEndTime());
        s.setClassName(dto.getClassName());
        s.setMaxMarks(dto.getMaxMarks());
        s.setRoom(dto.getRoom());
        s.setNotes(dto.getNotes());
        return toDTO(scheduleRepo.save(s));
    }

    @Override
    public void delete(Long id) {
        ExamSchedule s = scheduleRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam schedule not found: " + id));
        scheduleRepo.delete(s);
    }

    private ExamScheduleDTO toDTO(ExamSchedule s) {
        Exam exam = s.getExam();
        return ExamScheduleDTO.builder()
                .id(s.getId())
                .examId(exam != null ? exam.getId() : null)
                .examName(exam != null ? exam.getName() : null)
                .subject(s.getSubject())
                .examDate(s.getExamDate())
                .startTime(s.getStartTime())
                .endTime(s.getEndTime())
                .className(s.getClassName())
                .maxMarks(s.getMaxMarks())
                .room(s.getRoom())
                .notes(s.getNotes())
                .build();
    }
}
