package com.ailadeekshith.schoolManagement.controller;

import com.ailadeekshith.schoolManagement.model.Event;
import com.ailadeekshith.schoolManagement.model.EventPhoto;
import com.ailadeekshith.schoolManagement.service.EventService;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EventController {

    private final EventService eventService;

    // POST /api/events
    @PostMapping
    public ResponseEntity<Event> createEvent(@Valid @RequestBody Event event) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.createEvent(event));
    }

    // GET /api/events?status=&className=
    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents(
            @RequestParam(required = false) Event.EventStatus status,
            @RequestParam(required = false) String className) {
        return ResponseEntity.ok(eventService.filterEvents(status, className));
    }

    // GET /api/events/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    // PUT /api/events/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @Valid @RequestBody Event event) {
        return ResponseEntity.ok(eventService.updateEvent(id, event));
    }

    // DELETE /api/events/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    // PATCH /api/events/{id}/status?status=ACTIVE|ARCHIVED
    @PatchMapping("/{id}/status")
    public ResponseEntity<Event> updateStatus(@PathVariable Long id, @RequestParam Event.EventStatus status) {
        return ResponseEntity.ok(eventService.updateStatus(id, status));
    }

    // ── Photo gallery ────────────────────────────────────────────────

    // GET /api/events/{id}/photos
    @GetMapping("/{id}/photos")
    public ResponseEntity<List<EventPhoto>> getPhotos(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getPhotos(id));
    }

    // POST /api/events/{id}/photos
    @PostMapping("/{id}/photos")
    public ResponseEntity<EventPhoto> addPhoto(@PathVariable Long id, @RequestBody PhotoRequest req) {
        EventPhoto photo = EventPhoto.builder()
                .imageBase64(req.getImageBase64())
                .description(req.getDescription())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.addPhoto(id, photo));
    }

    // PUT /api/events/photos/{photoId}
    @PutMapping("/photos/{photoId}")
    public ResponseEntity<EventPhoto> updatePhoto(@PathVariable Long photoId, @RequestBody PhotoRequest req) {
        return ResponseEntity.ok(eventService.updatePhoto(photoId, req.getDescription()));
    }

    // DELETE /api/events/photos/{photoId}
    @DeleteMapping("/photos/{photoId}")
    public ResponseEntity<Void> deletePhoto(@PathVariable Long photoId) {
        eventService.deletePhoto(photoId);
        return ResponseEntity.noContent().build();
    }

    @Data
    public static class PhotoRequest {
        private String imageBase64;
        private String description;
    }
}
