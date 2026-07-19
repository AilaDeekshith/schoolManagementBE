package com.ailadeekshith.schoolManagement.service;

import com.ailadeekshith.schoolManagement.model.Syllabus;
import com.ailadeekshith.schoolManagement.model.SyllabusTopic;
import com.ailadeekshith.schoolManagement.model.TopicReference;

import java.util.List;

public interface SyllabusService {

    // ── Syllabus CRUD ─────────────────────────────────────────
    List<Syllabus> getAll();
    List<Syllabus> getByGrade(String gradeName);
    List<Syllabus> getByYear(String academicYear);
    Syllabus getById(Long id);
    Syllabus create(Syllabus syllabus);
    Syllabus update(Long id, Syllabus syllabus);
    void delete(Long id);

    // ── Topics ────────────────────────────────────────────────
    SyllabusTopic addTopic(Long syllabusId, SyllabusTopic topic);
    SyllabusTopic updateTopic(Long topicId, SyllabusTopic topic);
    void deleteTopic(Long topicId);
    SyllabusTopic patchStatus(Long topicId, SyllabusTopic.TopicStatus status);
    SyllabusTopic patchKeyTopic(Long topicId, boolean isKeyTopic);
    Syllabus moveTopic(Long topicId, String direction); // UP | DOWN

    // ── References ────────────────────────────────────────────
    TopicReference addReference(Long topicId, TopicReference reference);
    TopicReference updateReference(Long refId, TopicReference reference);
    void deleteReference(Long refId);
}
