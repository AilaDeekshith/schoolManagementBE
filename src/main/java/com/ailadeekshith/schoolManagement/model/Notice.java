package com.ailadeekshith.schoolManagement.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * A notice-board entry shown on the dashboard. Each notice has some
 * information text and an optional attached image.
 */
@Entity
@Table(name = "notice")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "image_base64", columnDefinition = "TEXT")
    private String imageBase64;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
