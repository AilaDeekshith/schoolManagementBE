package com.ailadeekshith.schoolManagement.controller;

import com.ailadeekshith.schoolManagement.model.Admission;
import com.ailadeekshith.schoolManagement.service.AdmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admissions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdmissionController {

    private final AdmissionService admissionService;

    // POST /api/admissions
    @PostMapping
    public ResponseEntity<Admission> createAdmission(@Valid @RequestBody Admission admission) {
        return ResponseEntity.status(HttpStatus.CREATED).body(admissionService.createAdmission(admission));
    }

    // GET /api/admissions
    @GetMapping
    public ResponseEntity<List<Admission>> getAllAdmissions() {
        return ResponseEntity.ok(admissionService.getAllAdmissions());
    }

    // GET /api/admissions/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Admission> getAdmissionById(@PathVariable Long id) {
        return ResponseEntity.ok(admissionService.getAdmissionById(id));
    }

    // PUT /api/admissions/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Admission> updateAdmission(@PathVariable Long id,
                                                     @Valid @RequestBody Admission admission) {
        return ResponseEntity.ok(admissionService.updateAdmission(id, admission));
    }

    // DELETE /api/admissions/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdmission(@PathVariable Long id) {
        admissionService.deleteAdmission(id);
        return ResponseEntity.noContent().build();
    }

    // PATCH /api/admissions/{id}/approve
    @PatchMapping("/{id}/approve")
    public ResponseEntity<Admission> approveAdmission(@PathVariable Long id) {
        return ResponseEntity.ok(admissionService.approveAdmission(id));
    }

    // PATCH /api/admissions/{id}/reject
    @PatchMapping("/{id}/reject")
    public ResponseEntity<Admission> rejectAdmission(@PathVariable Long id) {
        return ResponseEntity.ok(admissionService.rejectAdmission(id));
    }

    // PATCH /api/admissions/{id}/status
    @PatchMapping("/{id}/status")
    public ResponseEntity<Admission> updateStatus(@PathVariable Long id,
                                                  @RequestParam Admission.AdmissionStatus status) {
        return ResponseEntity.ok(admissionService.updateStatus(id, status));
    }

    // GET /api/admissions/status/{status}
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Admission>> getByStatus(@PathVariable Admission.AdmissionStatus status) {
        return ResponseEntity.ok(admissionService.getAdmissionsByStatus(status));
    }

    // GET /api/admissions/class/{applyClass}
    @GetMapping("/class/{applyClass}")
    public ResponseEntity<List<Admission>> getByClass(@PathVariable String applyClass) {
        return ResponseEntity.ok(admissionService.getAdmissionsByClass(applyClass));
    }

    // GET /api/admissions/search?name=xyz
    @GetMapping("/search")
    public ResponseEntity<List<Admission>> search(@RequestParam String name) {
        return ResponseEntity.ok(admissionService.searchByApplicantName(name));
    }

    // GET /api/admissions/count/pending
    @GetMapping("/count/pending")
    public ResponseEntity<Long> countPending() {
        return ResponseEntity.ok(admissionService.countPendingAdmissions());
    }
}
