package com.inkspire.inkspire.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;

@Entity
@Data
public class ProgressUpdate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // This acts as Update ID

    @Column(nullable = false)
    private String userId; // ID of the user who created this post

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProgressTemplate progressTemplate; // Predefined template (like WORKOUT, STUDY, POETRY etc.)

    @Column(length = 1000)
    private String description; // Short description about the progress update

    @Lob
    private String plan; // Full writing content (essay, poem, story, detailed plan)

    private Instant createdAt;
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}