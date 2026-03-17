package com.social.csocial.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "lost_found_items")
@Data
public class LostFoundItem {

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

    @Column(length = 120)
    private String imageUrl;

    @Column(nullable = false, length = 20)
    private String status = "LOST";

    @Column(name = "reporter_id", nullable = false)
    private Long reporterId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
