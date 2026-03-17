package com.social.csocial.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String nickname;
    private String avatar;
    private String profile;
    private String interests;

    @Column(unique = true)
    private String email;

    private String phone;
    private String college;
    private String major;
    private Integer grade;

    @Column(nullable = false)
    private String role = "USER";

    @Column(nullable = false)
    private String status = "ACTIVE";

    @Column(name = "alarm_enabled", nullable = false)
    private Boolean alarmEnabled = true;

    @Column(name = "alarm_radius_km", nullable = false)
    private Double alarmRadiusKm = 1.0;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public User() {
    }

    public User(String username, String password, String nickname) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
    }
}
