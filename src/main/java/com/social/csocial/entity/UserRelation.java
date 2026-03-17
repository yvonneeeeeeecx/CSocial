package com.social.csocial.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
        import java.time.LocalDateTime;

@Entity
@Table(name = "user_relations",
        uniqueConstraints = @UniqueConstraint(columnNames = {"from_user_id", "to_user_id", "relation_type"}))
@Data
public class UserRelation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "from_user_id", nullable = false)
    private Long fromUserId;    // 发起者

    @Column(name = "to_user_id", nullable = false)
    private Long toUserId;      // 接收者

    @Column(name = "relation_type", nullable = false)
    private String relationType; // "like"喜欢, "follow"关注, "block"屏蔽

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // 默认构造函数
    public UserRelation() {}

    // 带参构造函数
    public UserRelation(Long fromUserId, Long toUserId, String relationType) {
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.relationType = relationType;
    }
}