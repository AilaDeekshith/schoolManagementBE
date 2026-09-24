package com.ailadeekshith.schoolManagement.service;

import com.ailadeekshith.schoolManagement.model.Event;
import com.ailadeekshith.schoolManagement.model.EventPhoto;

import java.util.List;

public interface EventService {
    Event createEvent(Event event);
    Event getEventById(Long id);
    List<Event> filterEvents(Event.EventStatus status, String className);
    Event updateEvent(Long id, Event event);
    void deleteEvent(Long id);
    Event updateStatus(Long id, Event.EventStatus status);

    List<EventPhoto> getPhotos(Long eventId);
    EventPhoto addPhoto(Long eventId, EventPhoto photo);
    EventPhoto updatePhoto(Long photoId, String description);
    void deletePhoto(Long photoId);
}
