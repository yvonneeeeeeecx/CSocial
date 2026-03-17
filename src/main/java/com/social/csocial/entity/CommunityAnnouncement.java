package com.social.csocial.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "community_announcements")
@Data
public class CommunityAnnouncement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "community_id", nullable = false)
    private Long communityId;

    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, length = 2000)
    private String content;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
