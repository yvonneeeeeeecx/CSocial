package com.social.csocial.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "communities")
@Data
public class Community {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String name;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(nullable = false, length = 60)
    private String category;

    @Column(length = 120)
    private String location;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "max_members")
    private Integer maxMembers;

    @Column(nullable = false, length = 20)
    private String status = "OPEN";

    @Column(name = "join_approval_required", nullable = false)
    private Boolean joinApprovalRequired = true;

    @Column(name = "creator_id", nullable = false)
    private Long creatorId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
