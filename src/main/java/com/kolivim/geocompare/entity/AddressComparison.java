package com.kolivim.geocompare.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;


@Table(name = "address_comparison")
@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AddressComparison {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String address;

    private Double yandexLat;

    private Double yandexLon;

    private Double dadataLat;

    private Double dadataLon;

    private Double distanceMeters;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

}