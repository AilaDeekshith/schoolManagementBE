package com.ailadeekshith.schoolManagement.controller;

import com.ailadeekshith.schoolManagement.model.Fees;
import com.ailadeekshith.schoolManagement.service.FeesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fees")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FeesController {

    private final FeesService feesService;

    // POST /api/fees
    @PostMapping
    public ResponseEntity<Fees> createFeeRecord(@Valid @RequestBody Fees fees) {
        return ResponseEntity.status(HttpStatus.CREATED).body(feesService.createFeeRecord(fees));
    }

    // GET /api/fees
    @GetMapping
    public ResponseEntity<List<Fees>> getAllFees() {
        return ResponseEntity.ok(feesService.getAllFees());
    }

    // GET /api/fees/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Fees> getFeeById(@PathVariable Long id) {
        return ResponseEntity.ok(feesService.getFeeById(id));
    }

    // PUT /api/fees/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Fees> updateFee(@PathVariable Long id, @Valid @RequestBody Fees fees) {
        return ResponseEntity.ok(feesService.updateFee(id, fees));
    }

    // DELETE /api/fees/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFee(@PathVariable Long id) {
        feesService.deleteFee(id);
        return ResponseEntity.noContent().build();
    }

    // POST /api/fees/{id}/collect
    @PostMapping("/{id}/collect")
    public ResponseEntity<Fees> collectPayment(
            @PathVariable Long id,
            @RequestParam BigDecimal amount,
            @RequestParam Fees.PaymentMethod method,
            @RequestParam(required = false) String transactionId) {
        return ResponseEntity.ok(feesService.collectPayment(id, amount, method, transactionId));
    }

    // GET /api/fees/student/{studentId}
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Fees>> getByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(feesService.getFeesByStudent(studentId));
    }

    // GET /api/fees/status/{status}
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Fees>> getByStatus(@PathVariable Fees.FeeStatus status) {
        return ResponseEntity.ok(feesService.getFeesByStatus(status));
    }

    // GET /api/fees/year/{academicYear}
    @GetMapping("/year/{academicYear}")
    public ResponseEntity<List<Fees>> getByYear(@PathVariable String academicYear) {
        return ResponseEntity.ok(feesService.getFeesByAcademicYear(academicYear));
    }

    // GET /api/fees/summary
    @GetMapping("/summary")
    public ResponseEntity<Map<String, BigDecimal>> getSummary() {
        return ResponseEntity.ok(Map.of(
                "totalCollected",  feesService.getTotalCollected(),
                "totalOutstanding", feesService.getTotalOutstanding()
        ));
    }
}