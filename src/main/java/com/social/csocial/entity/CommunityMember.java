package com.social.csocial.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "community_members")
@Data
public class CommunityMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "community_id", nullable = false)
    private Long communityId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 20)
    private String role = "MEMBER";

    @Column(nullable = false, length = 20)
    private String status = "APPROVED";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
