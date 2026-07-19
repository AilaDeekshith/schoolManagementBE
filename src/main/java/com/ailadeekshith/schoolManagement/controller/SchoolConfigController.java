package com.ailadeekshith.schoolManagement.controller;

import com.ailadeekshith.schoolManagement.model.*;
import com.ailadeekshith.schoolManagement.service.SchoolConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SchoolConfigController {

    private final SchoolConfigService configService;

    // ── School Profile ────────────────────────────────────────
    @GetMapping("/profile")
    public ResponseEntity<SchoolProfile> getProfile() {
        return ResponseEntity.ok(configService.getProfile());
    }

    @PutMapping("/profile")
    public ResponseEntity<SchoolProfile> saveProfile(@RequestBody SchoolProfile profile) {
        return ResponseEntity.ok(configService.saveProfile(profile));
    }

    // ── Public branding (name + logo only, no auth required) ───
    @GetMapping("/branding")
    public ResponseEntity<Map<String, String>> getBranding() {
        SchoolProfile profile = configService.getProfile();
        Map<String, String> branding = new HashMap<>();
        branding.put("schoolName", profile.getSchoolName());
        branding.put("logoBase64", profile.getLogoBase64());
        return ResponseEntity.ok(branding);
    }

    // ── Receipt Templates ─────────────────────────────────────
    @GetMapping("/receipt-templates")
    public ResponseEntity<List<ReceiptTemplate>> getReceiptTemplates() {
        return ResponseEntity.ok(configService.getAllReceiptTemplates());
    }

    @PostMapping("/receipt-templates")
    public ResponseEntity<ReceiptTemplate> createReceiptTemplate(@RequestBody ReceiptTemplate template) {
        return ResponseEntity.status(HttpStatus.CREATED).body(configService.createReceiptTemplate(template));
    }

    @PutMapping("/receipt-templates/{id}")
    public ResponseEntity<ReceiptTemplate> updateReceiptTemplate(@PathVariable Long id,
                                                                 @RequestBody ReceiptTemplate template) {
        return ResponseEntity.ok(configService.updateReceiptTemplate(id, template));
    }

    @PatchMapping("/receipt-templates/{id}/default")
    public ResponseEntity<ReceiptTemplate> setDefaultReceiptTemplate(@PathVariable Long id) {
        return ResponseEntity.ok(configService.setDefaultReceiptTemplate(id));
    }

    @DeleteMapping("/receipt-templates/{id}")
    public ResponseEntity<Void> deleteReceiptTemplate(@PathVariable Long id) {
        configService.deleteReceiptTemplate(id);
        return ResponseEntity.noContent().build();
    }

    // ── Dashboard Slides (hero carousel) ──────────────────────
    @GetMapping("/dashboard-slides")
    public ResponseEntity<List<DashboardSlide>> getDashboardSlides() {
        return ResponseEntity.ok(configService.getAllDashboardSlides());
    }

    @PostMapping("/dashboard-slides")
    public ResponseEntity<DashboardSlide> createDashboardSlide(@RequestBody DashboardSlide slide) {
        return ResponseEntity.status(HttpStatus.CREATED).body(configService.createDashboardSlide(slide));
    }

    @PutMapping("/dashboard-slides/{id}")
    public ResponseEntity<DashboardSlide> updateDashboardSlide(@PathVariable Long id,
                                                               @RequestBody DashboardSlide slide) {
        return ResponseEntity.ok(configService.updateDashboardSlide(id, slide));
    }

    @DeleteMapping("/dashboard-slides/{id}")
    public ResponseEntity<Void> deleteDashboardSlide(@PathVariable Long id) {
        configService.deleteDashboardSlide(id);
        return ResponseEntity.noContent().build();
    }

    // ── Grades ────────────────────────────────────────────────
    @GetMapping("/grades")
    public ResponseEntity<List<Grade>> getGrades() {
        return ResponseEntity.ok(configService.getAllGrades());
    }

    @PostMapping("/grades")
    public ResponseEntity<Grade> createGrade(@RequestBody Grade grade) {
        return ResponseEntity.status(HttpStatus.CREATED).body(configService.createGrade(grade));
    }

    @DeleteMapping("/grades/{id}")
    public ResponseEntity<Void> deleteGrade(@PathVariable Long id) {
        configService.deleteGrade(id);
        return ResponseEntity.noContent().build();
    }

    // ── Sections ──────────────────────────────────────────────
    @PostMapping("/grades/{gradeId}/sections")
    public ResponseEntity<Section> addSection(@PathVariable Long gradeId,
                                              @RequestBody Map<String, String> body) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(configService.addSection(gradeId, body.get("letter")));
    }

    @DeleteMapping("/sections/{sectionId}")
    public ResponseEntity<Void> deleteSection(@PathVariable Long sectionId) {
        configService.deleteSection(sectionId);
        return ResponseEntity.noContent().build();
    }

    // ── Subjects ──────────────────────────────────────────────
    @GetMapping("/subjects")
    public ResponseEntity<List<Subject>> getSubjects() {
        return ResponseEntity.ok(configService.getAllSubjects());
    }

    @PostMapping("/subjects")
    public ResponseEntity<Subject> createSubject(@RequestBody Subject subject) {
        return ResponseEntity.status(HttpStatus.CREATED).body(configService.createSubject(subject));
    }

    @PutMapping("/subjects/{id}")
    public ResponseEntity<Subject> updateSubject(@PathVariable Long id,
                                                 @RequestBody Subject subject) {
        return ResponseEntity.ok(configService.updateSubject(id, subject));
    }

    @DeleteMapping("/subjects/{id}")
    public ResponseEntity<Void> deleteSubject(@PathVariable Long id) {
        configService.deleteSubject(id);
        return ResponseEntity.noContent().build();
    }

    // ── Fee Structure ─────────────────────────────────────────
    @GetMapping("/fee-structure")
    public ResponseEntity<List<FeeStructure>> getFeeStructures() {
        return ResponseEntity.ok(configService.getAllFeeStructures());
    }

    @PostMapping("/fee-structure")
    public ResponseEntity<FeeStructure> createFeeStructure(@RequestBody FeeStructure fs) {
        return ResponseEntity.status(HttpStatus.CREATED).body(configService.createFeeStructure(fs));
    }

    @PutMapping("/fee-structure/{id}")
    public ResponseEntity<FeeStructure> updateFeeStructure(@PathVariable Long id,
                                                           @RequestBody FeeStructure fs) {
        return ResponseEntity.ok(configService.updateFeeStructure(id, fs));
    }

    @DeleteMapping("/fee-structure/{id}")
    public ResponseEntity<Void> deleteFeeStructure(@PathVariable Long id) {
        configService.deleteFeeStructure(id);
        return ResponseEntity.noContent().build();
    }

    // ── Holidays ──────────────────────────────────────────────
    @GetMapping("/holidays")
    public ResponseEntity<List<Holiday>> getHolidays() {
        return ResponseEntity.ok(configService.getAllHolidays());
    }

    @PostMapping("/holidays")
    public ResponseEntity<Holiday> createHoliday(@RequestBody Holiday holiday) {
        return ResponseEntity.status(HttpStatus.CREATED).body(configService.createHoliday(holiday));
    }

    @PutMapping("/holidays/{id}")
    public ResponseEntity<Holiday> updateHoliday(@PathVariable Long id,
                                                 @RequestBody Holiday holiday) {
        return ResponseEntity.ok(configService.updateHoliday(id, holiday));
    }

    @DeleteMapping("/holidays/{id}")
    public ResponseEntity<Void> deleteHoliday(@PathVariable Long id) {
        configService.deleteHoliday(id);
        return ResponseEntity.noContent().build();
    }
}
