package com.iyzico.challenge.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class SeatPurchaseResponseTest {

    @Test
    void shouldCreateSeatPurchaseResponseWithBuilder() {
        // given
        Date now = new Date();

        // when
        SeatPurchaseResponse response = SeatPurchaseResponse.builder()
                .paymentId(1L)
                .flightId(10L)
                .seatId(100L)
                .seatNo("12A")
                .price(BigDecimal.valueOf(350))
                .passengerName("Kübra")
                .status("SUCCESS")
                .createdAt(now)
                .build();

        // then
        assertThat(response.getPaymentId()).isEqualTo(1L);
        assertThat(response.getFlightId()).isEqualTo(10L);
        assertThat(response.getSeatId()).isEqualTo(100L);
        assertThat(response.getSeatNo()).isEqualTo("12A");
        assertThat(response.getPrice()).isEqualTo(BigDecimal.valueOf(350));
        assertThat(response.getPassengerName()).isEqualTo("Kübra");
        assertThat(response.getStatus()).isEqualTo("SUCCESS");
        assertThat(response.getCreatedAt()).isEqualTo(now);
    }
}
