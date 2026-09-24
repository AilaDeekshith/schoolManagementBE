package com.ailadeekshith.schoolManagement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * A single photo added to an {@link Event}'s gallery, normally uploaded after the
 * event has taken place. Kept as its own entity (rather than a collection on Event)
 * so listing/loading events never has to pull every photo's base64 data along with it —
 * photos are only fetched when a specific event's gallery is opened.
 */
@Entity
@Table(name = "event_photos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    @JsonIgnore
    private Event event;

    @Column(name = "image_base64", nullable = false, columnDefinition = "TEXT")
    private String imageBase64;

    @Column(columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    @Column(name = "uploaded_at", updatable = false)
    private LocalDateTime uploadedAt;
}
