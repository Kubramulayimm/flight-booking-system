package com.iyzico.challenge.entity;

import com.iyzico.challenge.enums.SeatStatus;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Table(name = "seats",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_flight_seat_no", columnNames = {"flight_id", "seat_no"})
        },
        indexes = {
                @Index(name = "idx_seat_flight", columnList = "flight_id"),
                @Index(name = "idx_seat_status_deleted", columnList = "status,deleted")
        }
)
public class Seat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="seat_no", nullable = false, length = 10)
    private String seatNo;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SeatStatus status;

    @Version
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="flight_id", nullable = false)
    private Flight flight;
}