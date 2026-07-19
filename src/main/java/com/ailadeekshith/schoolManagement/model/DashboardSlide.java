package com.ailadeekshith.schoolManagement.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * A single slide in the dashboard hero carousel. Each slide pairs an
 * (optional) background image with a tagline. Admins can create as many
 * slides as they like; the dashboard rotates through them automatically.
 */
@Entity
@Table(name = "dashboard_slide")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardSlide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_base64", columnDefinition = "TEXT")
    private String imageBase64;

    @Column(name = "tagline")
    private String tagline;

    /** Lower numbers appear first in the carousel. */
    @Column(name = "sort_order")
    @Builder.Default
    private Integer sortOrder = 0;
}
