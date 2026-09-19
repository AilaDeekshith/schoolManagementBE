package com.ailadeekshith.schoolManagement.repository;

import com.ailadeekshith.schoolManagement.model.ClassDiaryEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClassDiaryEntryRepository extends JpaRepository<ClassDiaryEntry, Long> {

    Optional<ClassDiaryEntry> findByTimetable_IdAndDate(Long timetableId, LocalDate date);

    List<ClassDiaryEntry> findByClassNameAndDateOrderByCreatedAtAsc(String className, LocalDate date);

    List<ClassDiaryEntry> findByClassNameAndDateBetweenOrderByDateDescCreatedAtDesc(
            String className, LocalDate from, LocalDate to);

    List<ClassDiaryEntry> findByTeacher_IdOrderByDateDesc(Long teacherId);
}
