package com.social.csocial.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Data
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "from_user_id", nullable = false)
    private Long fromUserId;

    @Column(name = "to_user_id", nullable = false)
    private Long toUserId;

    @Column(nullable = false, length = 1000)
    private String content;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public Message() {
    }

    public Message(Long fromUserId, Long toUserId, String content) {
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.content = content;
    }
}
