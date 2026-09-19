package com.ailadeekshith.schoolManagement.service.impl;

import com.ailadeekshith.schoolManagement.exception.BadRequestException;
import com.ailadeekshith.schoolManagement.exception.ResourceNotFoundException;
import com.ailadeekshith.schoolManagement.model.ClassDiaryEntry;
import com.ailadeekshith.schoolManagement.model.Teacher;
import com.ailadeekshith.schoolManagement.model.TimeTable;
import com.ailadeekshith.schoolManagement.repository.ClassDiaryEntryRepository;
import com.ailadeekshith.schoolManagement.repository.TimeTableRepository;
import com.ailadeekshith.schoolManagement.service.ClassDiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ClassDiaryServiceImpl implements ClassDiaryService {

    private final ClassDiaryEntryRepository diaryRepo;
    private final TimeTableRepository timeTableRepo;

    @Override
    @Transactional(readOnly = true)
    public List<ClassDiaryEntry> getByClassAndDate(String className, LocalDate date) {
        return diaryRepo.findByClassNameAndDateOrderByCreatedAtAsc(className, date);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassDiaryEntry> getByClassAndDateRange(String className, LocalDate from, LocalDate to) {
        return diaryRepo.findByClassNameAndDateBetweenOrderByDateDescCreatedAtDesc(className, from, to);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassDiaryEntry> getByTeacher(Long teacherId) {
        return diaryRepo.findByTeacher_IdOrderByDateDesc(teacherId);
    }

    @Override
    @Transactional(readOnly = true)
    public ClassDiaryEntry getById(Long id) {
        return diaryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class diary entry not found: " + id));
    }

    @Override
    public ClassDiaryEntry save(Long timetableId, LocalDate date, Teacher teacher, ClassDiaryEntry data) {
        TimeTable timetable = timeTableRepo.findById(timetableId)
                .orElseThrow(() -> new BadRequestException("Timetable slot not found: " + timetableId));
        if (data.getTopicCovered() == null || data.getTopicCovered().isBlank()) {
            throw new BadRequestException("Topic covered is required");
        }

        ClassDiaryEntry entry = diaryRepo.findByTimetable_IdAndDate(timetableId, date)
                .orElseGet(() -> ClassDiaryEntry.builder()
                        .timetable(timetable)
                        .className(timetable.getClassName())
                        .subject(timetable.getSubject())
                        .date(date)
                        .build());

        entry.setTeacher(teacher);
        entry.setTopicCovered(data.getTopicCovered());
        entry.setSubTopics(data.getSubTopics());
        entry.setHomework(data.getHomework());
        entry.setInstructionsForNext(data.getInstructionsForNext());
        return diaryRepo.save(entry);
    }

    @Override
    public void delete(Long id) {
        diaryRepo.deleteById(id);
    }
}
