package io.github.pedrozaz.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity @Data
@Table(name = "restaurants")
@Builder
@NoArgsConstructor @AllArgsConstructor
public class Restaurant {
    @Id
    @Column(name = "restaurant_id")
    private String restaurantId;

    private String name;

    @Column(name = "cuisine_type")
    private String cuisineType;

    @Column(name = "price_range")
    private Integer priceRange;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
