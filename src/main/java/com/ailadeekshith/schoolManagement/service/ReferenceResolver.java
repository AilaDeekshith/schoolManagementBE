package com.ailadeekshith.schoolManagement.service;

import com.ailadeekshith.schoolManagement.model.Grade;
import com.ailadeekshith.schoolManagement.model.Section;
import com.ailadeekshith.schoolManagement.model.Subject;
import com.ailadeekshith.schoolManagement.repository.GradeRepository;
import com.ailadeekshith.schoolManagement.repository.SectionRepository;
import com.ailadeekshith.schoolManagement.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Resolves the legacy string values (class name like "Grade 10-A", grade name,
 * subject name) into their entity references, so the soft links can be backed
 * by real foreign keys.
 */
@Component
@RequiredArgsConstructor
public class ReferenceResolver {

    private final GradeRepository gradeRepo;
    private final SectionRepository sectionRepo;
    private final SubjectRepository subjectRepo;

    private boolean isAll(String s) {
        if (s == null) return true;
        String t = s.trim();
        return t.isEmpty() || t.equalsIgnoreCase("all") || t.equalsIgnoreCase("all grades")
                || t.equalsIgnoreCase("all subjects") || t.equalsIgnoreCase("all classes");
    }

    /** "Grade 10-A" -> the matching Section (grade "Grade 10" + letter "A"). */
    public Section resolveSection(String className) {
        if (isAll(className)) return null;
        String cn = className.trim();
        int dash = cn.lastIndexOf('-');
        if (dash <= 0 || dash == cn.length() - 1) return null;
        Grade grade = resolveGrade(cn.substring(0, dash));
        if (grade == null) return null;
        return sectionRepo.findByGradeIdAndLetterIgnoreCase(grade.getId(), cn.substring(dash + 1).trim()).orElse(null);
    }

    public Grade resolveGrade(String gradeName) {
        if (isAll(gradeName)) return null;
        return gradeRepo.findByNameIgnoreCase(gradeName.trim()).orElse(null);
    }

    public Subject resolveSubject(String subjectName) {
        if (isAll(subjectName)) return null;
        return subjectRepo.findByNameIgnoreCase(subjectName.trim()).orElse(null);
    }
}
