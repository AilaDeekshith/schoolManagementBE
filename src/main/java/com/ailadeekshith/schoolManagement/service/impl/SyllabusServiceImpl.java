package com.ailadeekshith.schoolManagement.service.impl;

import com.ailadeekshith.schoolManagement.exception.BadRequestException;
import com.ailadeekshith.schoolManagement.exception.DuplicateResourceException;
import com.ailadeekshith.schoolManagement.exception.ResourceNotFoundException;
import com.ailadeekshith.schoolManagement.model.Syllabus;
import com.ailadeekshith.schoolManagement.model.SyllabusTopic;
import com.ailadeekshith.schoolManagement.model.TopicReference;
import com.ailadeekshith.schoolManagement.repository.SyllabusRepository;
import com.ailadeekshith.schoolManagement.repository.SyllabusTopicRepository;
import com.ailadeekshith.schoolManagement.repository.TopicReferenceRepository;
import com.ailadeekshith.schoolManagement.service.SyllabusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SyllabusServiceImpl implements SyllabusService {

    private final SyllabusRepository syllabusRepo;
    private final SyllabusTopicRepository topicRepo;
    private final TopicReferenceRepository refRepo;

    // ── Syllabus CRUD ─────────────────────────────────────────────────

    @Override @Transactional(readOnly = true)
    public List<Syllabus> getAll() {
        return syllabusRepo.findAll();
    }

    @Override @Transactional(readOnly = true)
    public List<Syllabus> getByGrade(String gradeName) {
        return syllabusRepo.findByGradeNameOrderBySubjectNameAsc(gradeName);
    }

    @Override @Transactional(readOnly = true)
    public List<Syllabus> getByYear(String academicYear) {
        return syllabusRepo.findByAcademicYearOrderByGradeNameAscSubjectNameAsc(academicYear);
    }

    @Override @Transactional(readOnly = true)
    public Syllabus getById(Long id) {
        return syllabusRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Syllabus not found: " + id));
    }

    @Override
    public Syllabus create(Syllabus syllabus) {
        if (syllabusRepo.existsByGradeNameAndSubjectNameAndAcademicYear(
                syllabus.getGradeName(), syllabus.getSubjectName(), syllabus.getAcademicYear())) {
            throw new DuplicateResourceException(
                    "Syllabus already exists for " + syllabus.getGradeName()
                    + " › " + syllabus.getSubjectName()
                    + " (" + syllabus.getAcademicYear() + ")");
        }
        log.info("Creating syllabus: {} › {}", syllabus.getGradeName(), syllabus.getSubjectName());
        return syllabusRepo.save(syllabus);
    }

    @Override
    public Syllabus update(Long id, Syllabus incoming) {
        Syllabus existing = getById(id);
        existing.setGradeName(incoming.getGradeName());
        existing.setSubjectName(incoming.getSubjectName());
        existing.setAcademicYear(incoming.getAcademicYear());
        existing.setDescription(incoming.getDescription());
        existing.setTotalHours(incoming.getTotalHours());
        return syllabusRepo.save(existing);
    }

    @Override
    public void delete(Long id) {
        if (!syllabusRepo.existsById(id))
            throw new ResourceNotFoundException("Syllabus not found: " + id);
        syllabusRepo.deleteById(id);
    }

    // ── Topics ────────────────────────────────────────────────────────

    @Override
    public SyllabusTopic addTopic(Long syllabusId, SyllabusTopic topic) {
        Syllabus syllabus = getById(syllabusId);
        int nextIndex = topicRepo.findMaxOrderIndex(syllabusId) + 1;
        topic.setSyllabus(syllabus);
        topic.setOrderIndex(nextIndex);
        if (topic.getStatus() == null) topic.setStatus(SyllabusTopic.TopicStatus.NOT_STARTED);
        if (topic.getIsKeyTopic() == null) topic.setIsKeyTopic(false);
        return topicRepo.save(topic);
    }

    @Override
    public SyllabusTopic updateTopic(Long topicId, SyllabusTopic incoming) {
        SyllabusTopic t = topicRepo.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found: " + topicId));
        t.setTitle(incoming.getTitle());
        t.setDescription(incoming.getDescription());
        t.setEstimatedHours(incoming.getEstimatedHours());
        t.setIsKeyTopic(incoming.getIsKeyTopic() != null && incoming.getIsKeyTopic());
        if (incoming.getStatus() != null) t.setStatus(incoming.getStatus());
        return topicRepo.save(t);
    }

    @Override
    public void deleteTopic(Long topicId) {
        if (!topicRepo.existsById(topicId))
            throw new ResourceNotFoundException("Topic not found: " + topicId);
        topicRepo.deleteById(topicId);
    }

    @Override
    public SyllabusTopic patchStatus(Long topicId, SyllabusTopic.TopicStatus status) {
        SyllabusTopic t = topicRepo.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found: " + topicId));
        t.setStatus(status);
        return topicRepo.save(t);
    }

    @Override
    public SyllabusTopic patchKeyTopic(Long topicId, boolean isKeyTopic) {
        SyllabusTopic t = topicRepo.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found: " + topicId));
        t.setIsKeyTopic(isKeyTopic);
        return topicRepo.save(t);
    }

    @Override
    public Syllabus moveTopic(Long topicId, String direction) {
        SyllabusTopic topic = topicRepo.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found: " + topicId));
        Long syllabusId = topic.getSyllabus().getId();

        List<SyllabusTopic> ordered = topicRepo.findBySyllabusIdOrderByOrderIndexAsc(syllabusId);
        int pos = -1;
        for (int i = 0; i < ordered.size(); i++) {
            if (ordered.get(i).getId().equals(topicId)) { pos = i; break; }
        }
        if (pos == -1) throw new BadRequestException("Topic does not belong to this syllabus");

        if ("UP".equalsIgnoreCase(direction) && pos > 0) {
            SyllabusTopic prev = ordered.get(pos - 1);
            int tmpIdx = prev.getOrderIndex();
            prev.setOrderIndex(topic.getOrderIndex());
            topic.setOrderIndex(tmpIdx);
            topicRepo.save(prev);
            topicRepo.save(topic);
        } else if ("DOWN".equalsIgnoreCase(direction) && pos < ordered.size() - 1) {
            SyllabusTopic next = ordered.get(pos + 1);
            int tmpIdx = next.getOrderIndex();
            next.setOrderIndex(topic.getOrderIndex());
            topic.setOrderIndex(tmpIdx);
            topicRepo.save(next);
            topicRepo.save(topic);
        }
        // Re-fetch fresh
        return syllabusRepo.findById(syllabusId).orElseThrow();
    }

    // ── References ────────────────────────────────────────────────────

    @Override
    public TopicReference addReference(Long topicId, TopicReference ref) {
        SyllabusTopic topic = topicRepo.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found: " + topicId));
        ref.setTopic(topic);
        if (ref.getType() == null) ref.setType(TopicReference.ReferenceType.WEBSITE);
        return refRepo.save(ref);
    }

    @Override
    public TopicReference updateReference(Long refId, TopicReference incoming) {
        TopicReference ref = refRepo.findById(refId)
                .orElseThrow(() -> new ResourceNotFoundException("Reference not found: " + refId));
        ref.setTitle(incoming.getTitle());
        ref.setUrl(incoming.getUrl());
        ref.setType(incoming.getType());
        ref.setDescription(incoming.getDescription());
        return refRepo.save(ref);
    }

    @Override
    public void deleteReference(Long refId) {
        if (!refRepo.existsById(refId))
            throw new ResourceNotFoundException("Reference not found: " + refId);
        refRepo.deleteById(refId);
    }
}
