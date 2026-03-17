package com.social.csocial.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_comments")
@Data
public class PostComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 500)
    private String content;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
