package com.ailadeekshith.schoolManagement.config;

import com.ailadeekshith.schoolManagement.repository.*;
import com.ailadeekshith.schoolManagement.service.ReferenceResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * One-time (idempotent) backfill that populates the new foreign keys from the
 * legacy string columns. Runs on every startup but only fills FKs that are
 * still null, so it's safe to keep and cheap to re-run.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SoftLinkBackfill implements ApplicationRunner {

    private final ReferenceResolver resolver;
    private final StudentRepository studentRepo;
    private final AttendanceRepository attendanceRepo;
    private final ExamRepository examRepo;
    private final ExamScheduleRepository examScheduleRepo;
    private final TimeTableRepository timeTableRepo;
    private final TimetablePeriodRepository timetablePeriodRepo;
    private final FeeStructureRepository feeStructureRepo;
    private final SyllabusRepository syllabusRepo;
    private final ExamResultRepository examResultRepo;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        int[] n = {0};

        studentRepo.findAll().forEach(s -> {
            if (s.getSection() == null) {
                var sec = resolver.resolveSection(s.getClassName());
                if (sec != null) { s.setSection(sec); studentRepo.save(s); n[0]++; }
            }
        });
        attendanceRepo.findAll().forEach(a -> {
            if (a.getSection() == null) {
                var sec = resolver.resolveSection(a.getClassName());
                if (sec != null) { a.setSection(sec); attendanceRepo.save(a); n[0]++; }
            }
        });
        examRepo.findAll().forEach(e -> {
            boolean changed = false;
            if (e.getSection() == null) { var sec = resolver.resolveSection(e.getClassName()); if (sec != null) { e.setSection(sec); changed = true; } }
            if (e.getSubjectRef() == null) { var sub = resolver.resolveSubject(e.getSubject()); if (sub != null) { e.setSubjectRef(sub); changed = true; } }
            if (changed) { examRepo.save(e); n[0]++; }
        });
        examScheduleRepo.findAll().forEach(e -> {
            boolean changed = false;
            if (e.getSection() == null) { var sec = resolver.resolveSection(e.getClassName()); if (sec != null) { e.setSection(sec); changed = true; } }
            if (e.getSubjectRef() == null) { var sub = resolver.resolveSubject(e.getSubject()); if (sub != null) { e.setSubjectRef(sub); changed = true; } }
            if (changed) { examScheduleRepo.save(e); n[0]++; }
        });
        timeTableRepo.findAll().forEach(t -> {
            boolean changed = false;
            if (t.getSection() == null) { var sec = resolver.resolveSection(t.getClassName()); if (sec != null) { t.setSection(sec); changed = true; } }
            if (t.getSubjectRef() == null) { var sub = resolver.resolveSubject(t.getSubject()); if (sub != null) { t.setSubjectRef(sub); changed = true; } }
            if (changed) { timeTableRepo.save(t); n[0]++; }
        });
        timetablePeriodRepo.findAll().forEach(p -> {
            if (p.getSection() == null) {
                var sec = resolver.resolveSection(p.getClassName());
                if (sec != null) { p.setSection(sec); timetablePeriodRepo.save(p); n[0]++; }
            }
        });
        feeStructureRepo.findAll().forEach(f -> {
            if (f.getGrade() == null) {
                var g = resolver.resolveGrade(f.getGradeName());
                if (g != null) { f.setGrade(g); feeStructureRepo.save(f); n[0]++; }
            }
        });
        syllabusRepo.findAll().forEach(s -> {
            boolean changed = false;
            if (s.getGrade() == null) { var g = resolver.resolveGrade(s.getGradeName()); if (g != null) { s.setGrade(g); changed = true; } }
            if (s.getSubject() == null) { var sub = resolver.resolveSubject(s.getSubjectName()); if (sub != null) { s.setSubject(sub); changed = true; } }
            if (changed) { syllabusRepo.save(s); n[0]++; }
        });
        examResultRepo.findAll().forEach(r -> {
            if (r.getSubjectRef() == null) {
                var sub = resolver.resolveSubject(r.getSubject());
                if (sub != null) { r.setSubjectRef(sub); examResultRepo.save(r); n[0]++; }
            }
        });

        if (n[0] > 0) log.info("SoftLinkBackfill: populated {} foreign keys from legacy strings", n[0]);
    }
}
