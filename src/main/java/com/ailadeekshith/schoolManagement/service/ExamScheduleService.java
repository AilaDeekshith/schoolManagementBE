package com.ailadeekshith.schoolManagement.service;

import com.ailadeekshith.schoolManagement.dto.ExamScheduleDTO;

import java.util.List;

public interface ExamScheduleService {
    List<ExamScheduleDTO> getByExam(Long examId);
    ExamScheduleDTO create(Long examId, ExamScheduleDTO dto);
    ExamScheduleDTO update(Long id, ExamScheduleDTO dto);
    void delete(Long id);
}
