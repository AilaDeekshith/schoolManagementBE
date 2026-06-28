package com.ailadeekshith.schoolManagement.service;

import com.ailadeekshith.schoolManagement.model.*;

import java.util.List;

public interface SchoolConfigService {

    // ── Profile ───────────────────────────────────────────────
    SchoolProfile getProfile();
    SchoolProfile saveProfile(SchoolProfile profile);

    // ── Grades ────────────────────────────────────────────────
    List<Grade> getAllGrades();
    Grade createGrade(Grade grade);
    void deleteGrade(Long id);

    // ── Sections ──────────────────────────────────────────────
    Section addSection(Long gradeId, String letter);
    void deleteSection(Long sectionId);

    // ── Subjects ──────────────────────────────────────────────
    List<Subject> getAllSubjects();
    Subject createSubject(Subject subject);
    Subject updateSubject(Long id, Subject subject);
    void deleteSubject(Long id);

    // ── Fee Structure ─────────────────────────────────────────
    List<FeeStructure> getAllFeeStructures();
    FeeStructure createFeeStructure(FeeStructure fs);
    FeeStructure updateFeeStructure(Long id, FeeStructure fs);
    void deleteFeeStructure(Long id);

    // ── Holidays ──────────────────────────────────────────────
    List<Holiday> getAllHolidays();
    Holiday createHoliday(Holiday holiday);
    Holiday updateHoliday(Long id, Holiday holiday);
    void deleteHoliday(Long id);
}
