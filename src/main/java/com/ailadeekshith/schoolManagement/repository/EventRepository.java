package com.ailadeekshith.schoolManagement.repository;

import com.ailadeekshith.schoolManagement.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // Combined, all-optional filter run entirely in the database.
    // A null argument means "don't filter on this field".
    @Query("SELECT e FROM Event e WHERE " +
           "(:status IS NULL OR e.status = :status) AND " +
           "(:className IS NULL OR :className MEMBER OF e.classes OR e.classes IS EMPTY) " +
           "ORDER BY e.eventDate DESC")
    List<Event> filterEvents(Event.EventStatus status, String className);

    // Every event whose classes list is empty (whole-school) plus any event
    // targeting one of the given class names — used to scope a teacher's or
    // student's own event feed to what's actually relevant to them.
    @Query("SELECT DISTINCT e FROM Event e LEFT JOIN e.classes c " +
           "WHERE e.classes IS EMPTY OR c IN :classNames " +
           "ORDER BY e.eventDate DESC")
    List<Event> findVisibleToClasses(Collection<String> classNames);
}
