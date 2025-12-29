package com.iyzico.challenge.dto;

import com.iyzico.challenge.enums.SeatStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class SeatResponseTest {

    @Test
    void shouldCreateSeatResponseWithBuilder() {
        // when
        SeatResponse response = SeatResponse.builder()
                .id(1L)
                .flightId(10L)
                .seatNo("12A")
                .price(BigDecimal.valueOf(300))
                .status(SeatStatus.AVAILABLE)
                .build();

        // then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getFlightId()).isEqualTo(10L);
        assertThat(response.getSeatNo()).isEqualTo("12A");
        assertThat(response.getPrice()).isEqualTo(BigDecimal.valueOf(300));
        assertThat(response.getStatus()).isEqualTo(SeatStatus.AVAILABLE);
    }
}
