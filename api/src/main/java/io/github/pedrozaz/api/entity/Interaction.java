package io.github.pedrozaz.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity @Data
@Table(name = "interactions")
@Builder
@NoArgsConstructor @AllArgsConstructor
public class Interaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interaction_id")
    private Long interactionId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "restaurant_id")
    private String restaurantId;

    private Integer rating;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
