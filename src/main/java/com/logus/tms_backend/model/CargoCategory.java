package com.logus.tms_backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cargo_categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CargoCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // Название категории (например, "Продукты питания")

    @Column(length = 500)
    private String description; // Описание (требования к перевозке)

    @Column(nullable = false)
    private Boolean requiresSpecialPermit = false; // Требуются ли спец. разрешения
}