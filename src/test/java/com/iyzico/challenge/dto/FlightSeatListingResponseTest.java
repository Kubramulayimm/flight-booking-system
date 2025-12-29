package com.iyzico.challenge.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FlightSeatListingResponseTest {

    @Test
    void builder_should_create_flightSeatListingResponse() {
        FlightSeatListingResponse.AvailableSeatItem seat1 =
                FlightSeatListingResponse.AvailableSeatItem.builder()
                        .seatId(1L)
                        .seatNo("1A")
                        .price(new BigDecimal("100.00"))
                        .build();

        FlightSeatListingResponse.AvailableSeatItem seat2 =
                FlightSeatListingResponse.AvailableSeatItem.builder()
                        .seatId(2L)
                        .seatNo("1B")
                        .price(new BigDecimal("120.50"))
                        .build();

        FlightSeatListingResponse response = FlightSeatListingResponse.builder()
                .flightId(10L)
                .flightCode("TK101")
                .flightName("Istanbul - Ankara")
                .description("Morning flight")
                .availableSeats(List.of(seat1, seat2))
                .build();

        assertEquals(10L, response.getFlightId());
        assertEquals("TK101", response.getFlightCode());
        assertEquals("Istanbul - Ankara", response.getFlightName());
        assertEquals("Morning flight", response.getDescription());

        assertNotNull(response.getAvailableSeats());
        assertEquals(2, response.getAvailableSeats().size());

        FlightSeatListingResponse.AvailableSeatItem item1 = response.getAvailableSeats().get(0);
        assertEquals(1L, item1.getSeatId());
        assertEquals("1A", item1.getSeatNo());
        assertEquals(new BigDecimal("100.00"), item1.getPrice());

        FlightSeatListingResponse.AvailableSeatItem item2 = response.getAvailableSeats().get(1);
        assertEquals(2L, item2.getSeatId());
        assertEquals("1B", item2.getSeatNo());
        assertEquals(new BigDecimal("120.50"), item2.getPrice());
    }

    @Test
    void setters_should_update_fields() {
        FlightSeatListingResponse response = new FlightSeatListingResponse();

        response.setFlightId(20L);
        response.setFlightCode("TK202");
        response.setFlightName("Izmir - Istanbul");
        response.setDescription("Evening flight");

        assertEquals(20L, response.getFlightId());
        assertEquals("TK202", response.getFlightCode());
        assertEquals("Izmir - Istanbul", response.getFlightName());
        assertEquals("Evening flight", response.getDescription());
    }

    @Test
    void availableSeatItem_setters_should_work() {
        FlightSeatListingResponse.AvailableSeatItem item =
                new FlightSeatListingResponse.AvailableSeatItem();

        item.setSeatId(5L);
        item.setSeatNo("3C");
        item.setPrice(new BigDecimal("150.00"));

        assertEquals(5L, item.getSeatId());
        assertEquals("3C", item.getSeatNo());
        assertEquals(new BigDecimal("150.00"), item.getPrice());
    }
}
