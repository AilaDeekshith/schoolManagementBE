package com.ailadeekshith.schoolManagement.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "syllabus_topics")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyllabusTopic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "syllabus_id", nullable = false)
    @JsonBackReference("syllabus-topics")
    private Syllabus syllabus;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "order_index")
    @Builder.Default
    private Integer orderIndex = 0;

    @Column(name = "estimated_hours")
    private Integer estimatedHours;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TopicStatus status = TopicStatus.NOT_STARTED;

    @Column(name = "is_key_topic")
    @Builder.Default
    private Boolean isKeyTopic = false;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL,
               orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("id ASC")
    @JsonManagedReference("topic-references")
    @Builder.Default
    private List<TopicReference> references = new ArrayList<>();

    public enum TopicStatus { NOT_STARTED, IN_PROGRESS, COMPLETED }
}
