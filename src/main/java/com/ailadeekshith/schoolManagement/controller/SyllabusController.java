package com.ailadeekshith.schoolManagement.controller;

import com.ailadeekshith.schoolManagement.model.Syllabus;
import com.ailadeekshith.schoolManagement.model.SyllabusTopic;
import com.ailadeekshith.schoolManagement.model.TopicReference;
import com.ailadeekshith.schoolManagement.service.SyllabusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/syllabi")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SyllabusController {

    private final SyllabusService syllabusService;

    // ── Syllabus CRUD ─────────────────────────────────────────────────

    /** GET /api/syllabi  |  ?grade=Grade+9  |  ?year=2024-25 */
    @GetMapping
    public ResponseEntity<List<Syllabus>> getAll(
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String year) {
        if (grade != null && !grade.isBlank())
            return ResponseEntity.ok(syllabusService.getByGrade(grade));
        if (year != null && !year.isBlank())
            return ResponseEntity.ok(syllabusService.getByYear(year));
        return ResponseEntity.ok(syllabusService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Syllabus> getById(@PathVariable Long id) {
        return ResponseEntity.ok(syllabusService.getById(id));
    }

    @PostMapping
    public ResponseEntity<Syllabus> create(@RequestBody Syllabus syllabus) {
        return ResponseEntity.status(HttpStatus.CREATED).body(syllabusService.create(syllabus));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Syllabus> update(@PathVariable Long id, @RequestBody Syllabus syllabus) {
        return ResponseEntity.ok(syllabusService.update(id, syllabus));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        syllabusService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ── Topics ────────────────────────────────────────────────────────

    @PostMapping("/{syllabusId}/topics")
    public ResponseEntity<SyllabusTopic> addTopic(@PathVariable Long syllabusId,
                                                   @RequestBody SyllabusTopic topic) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(syllabusService.addTopic(syllabusId, topic));
    }

    @PutMapping("/topics/{topicId}")
    public ResponseEntity<SyllabusTopic> updateTopic(@PathVariable Long topicId,
                                                      @RequestBody SyllabusTopic topic) {
        return ResponseEntity.ok(syllabusService.updateTopic(topicId, topic));
    }

    @DeleteMapping("/topics/{topicId}")
    public ResponseEntity<Void> deleteTopic(@PathVariable Long topicId) {
        syllabusService.deleteTopic(topicId);
        return ResponseEntity.noContent().build();
    }

    /** PATCH /api/syllabi/topics/{id}/status?status=IN_PROGRESS */
    @PatchMapping("/topics/{topicId}/status")
    public ResponseEntity<SyllabusTopic> patchStatus(
            @PathVariable Long topicId,
            @RequestParam SyllabusTopic.TopicStatus status) {
        return ResponseEntity.ok(syllabusService.patchStatus(topicId, status));
    }

    /** PATCH /api/syllabi/topics/{id}/key?flag=true */
    @PatchMapping("/topics/{topicId}/key")
    public ResponseEntity<SyllabusTopic> patchKey(
            @PathVariable Long topicId,
            @RequestParam boolean flag) {
        return ResponseEntity.ok(syllabusService.patchKeyTopic(topicId, flag));
    }

    /** PATCH /api/syllabi/topics/{id}/move?direction=UP */
    @PatchMapping("/topics/{topicId}/move")
    public ResponseEntity<Syllabus> moveTopic(
            @PathVariable Long topicId,
            @RequestParam String direction) {
        return ResponseEntity.ok(syllabusService.moveTopic(topicId, direction));
    }

    // ── References ────────────────────────────────────────────────────

    @PostMapping("/topics/{topicId}/references")
    public ResponseEntity<TopicReference> addReference(@PathVariable Long topicId,
                                                        @RequestBody TopicReference reference) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(syllabusService.addReference(topicId, reference));
    }

    @PutMapping("/references/{refId}")
    public ResponseEntity<TopicReference> updateReference(@PathVariable Long refId,
                                                           @RequestBody TopicReference reference) {
        return ResponseEntity.ok(syllabusService.updateReference(refId, reference));
    }

    @DeleteMapping("/references/{refId}")
    public ResponseEntity<Void> deleteReference(@PathVariable Long refId) {
        syllabusService.deleteReference(refId);
        return ResponseEntity.noContent().build();
    }
}
