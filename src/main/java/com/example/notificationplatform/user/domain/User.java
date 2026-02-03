package com.example.notificationplatform.user.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_email", columnNames = {"email"})
        },
        indexes = {
                @Index(name = "ix_users_created_at", columnList = "created_at")
        }
)
public class User {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "email", nullable = false, length = 320)
    private String email;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public User(UUID id, String email, String name, Instant createdAt) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.createdAt = createdAt;
    }

    private static String normalizeEmail(String email){
        if (email == null) throw new IllegalArgumentException("email is null");
        return email.trim().toLowerCase();
    }

    public void rename(String newName){
        if (newName == null || newName.isBlank()){
            throw new IllegalArgumentException("name is blank");
        }
        this.name = name;
    }
}