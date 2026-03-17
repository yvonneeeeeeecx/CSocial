package com.social.csocial.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "second_hand_items")
@Data
public class SecondHandItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, length = 2000)
    private String content;

    @Column(nullable = false, length = 60)
    private String category;

    @Column(nullable = false)
    private Double price;

    private String imageUrl;

    @Column(length = 120)
    private String contact;

    @Column(nullable = false, length = 20)
    private String status = "ON_SALE";

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
