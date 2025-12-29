package com.iyzico.challenge.entity;

import lombok.*;
import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "flights",
        indexes = {
                @Index(name = "idx_flight_code", columnList = "flight_code"),
                @Index(name = "idx_flight_deleted", columnList = "deleted")
        }
)
public class Flight extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "flight_code", nullable = false, length = 15, unique = true)
    private String flightCode;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "description", length = 500)
    private String description;
}


