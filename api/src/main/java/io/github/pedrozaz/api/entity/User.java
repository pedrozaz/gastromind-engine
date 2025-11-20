package io.github.pedrozaz.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity @Data
@Table(name = "users")
@NoArgsConstructor @AllArgsConstructor
public class User {
    @Id
    @Column(name = "user_id")
    private String userId;

    private String name;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
