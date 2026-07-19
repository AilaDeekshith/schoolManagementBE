package com.ailadeekshith.schoolManagement.controller;

import com.ailadeekshith.schoolManagement.model.Notice;
import com.ailadeekshith.schoolManagement.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping
    public ResponseEntity<List<Notice>> getAll() {
        return ResponseEntity.ok(noticeService.getAll());
    }

    @PostMapping
    public ResponseEntity<Notice> create(@RequestBody Notice notice) {
        return ResponseEntity.status(HttpStatus.CREATED).body(noticeService.create(notice));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Notice> update(@PathVariable Long id, @RequestBody Notice notice) {
        return ResponseEntity.ok(noticeService.update(id, notice));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        noticeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
