package com.ailadeekshith.schoolManagement.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "topic_references")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopicReference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    @JsonBackReference("topic-references")
    private SyllabusTopic topic;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String url;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ReferenceType type = ReferenceType.WEBSITE;

    @Column(columnDefinition = "TEXT")
    private String description;

    public enum ReferenceType { WEBSITE, YOUTUBE, PDF, DOCUMENT, OTHER }
}
