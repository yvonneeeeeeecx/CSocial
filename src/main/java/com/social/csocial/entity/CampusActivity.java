package com.social.csocial.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "campus_activities")
@Data
public class CampusActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, length = 2000)
    private String content;

    @Column(nullable = false, length = 60)
    private String category;

    @Column(length = 120)
    private String location;

    @Column(length = 120)
    private String contact;

    @Column(name = "organizer_id", nullable = false)
    private Long organizerId;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
