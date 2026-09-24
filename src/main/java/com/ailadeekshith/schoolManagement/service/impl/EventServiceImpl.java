package com.ailadeekshith.schoolManagement.service.impl;

import com.ailadeekshith.schoolManagement.exception.ResourceNotFoundException;
import com.ailadeekshith.schoolManagement.model.Event;
import com.ailadeekshith.schoolManagement.model.EventPhoto;
import com.ailadeekshith.schoolManagement.repository.EventPhotoRepository;
import com.ailadeekshith.schoolManagement.repository.EventRepository;
import com.ailadeekshith.schoolManagement.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventPhotoRepository eventPhotoRepository;

    @Override
    public Event createEvent(Event event) {
        // Event.status's "= ACTIVE" field initializer only runs through the Lombok
        // builder; Jackson deserializes @RequestBody via the no-args constructor +
        // setters, so a request that omits "status" leaves this null unless we
        // default it here explicitly.
        if (event.getStatus() == null) {
            event.setStatus(Event.EventStatus.ACTIVE);
        }
        log.info("Creating event: {}", event.getName());
        return eventRepository.save(event);
    }

    @Override
    @Transactional(readOnly = true)
    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> filterEvents(Event.EventStatus status, String className) {
        String cn = (className == null || className.isBlank()) ? null : className;
        return eventRepository.filterEvents(status, cn);
    }

    @Override
    public Event updateEvent(Long id, Event updated) {
        Event existing = getEventById(id);
        existing.setName(updated.getName());
        existing.setEventDate(updated.getEventDate());
        existing.setEventTime(updated.getEventTime());
        existing.setClasses(updated.getClasses());
        existing.setDescription(updated.getDescription());
        existing.setChiefGuest(updated.getChiefGuest());
        existing.setInstructions(updated.getInstructions());
        log.info("Updated event id: {}", id);
        return eventRepository.save(existing);
    }

    @Override
    public void deleteEvent(Long id) {
        Event event = getEventById(id);
        eventPhotoRepository.deleteByEventId(id);
        eventRepository.delete(event);
        log.info("Deleted event id: {}", id);
    }

    @Override
    public Event updateStatus(Long id, Event.EventStatus status) {
        Event event = getEventById(id);
        event.setStatus(status);
        log.info("Updated event id: {} to status: {}", id, status);
        return eventRepository.save(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventPhoto> getPhotos(Long eventId) {
        getEventById(eventId); // 404 if the event doesn't exist
        return eventPhotoRepository.findByEventIdOrderByIdAsc(eventId);
    }

    @Override
    public EventPhoto addPhoto(Long eventId, EventPhoto photo) {
        Event event = getEventById(eventId);
        photo.setEvent(event);
        log.info("Added photo to event id: {}", eventId);
        return eventPhotoRepository.save(photo);
    }

    @Override
    public EventPhoto updatePhoto(Long photoId, String description) {
        EventPhoto photo = eventPhotoRepository.findById(photoId)
                .orElseThrow(() -> new ResourceNotFoundException("Event photo not found with id: " + photoId));
        photo.setDescription(description);
        return eventPhotoRepository.save(photo);
    }

    @Override
    public void deletePhoto(Long photoId) {
        EventPhoto photo = eventPhotoRepository.findById(photoId)
                .orElseThrow(() -> new ResourceNotFoundException("Event photo not found with id: " + photoId));
        eventPhotoRepository.delete(photo);
    }
}
