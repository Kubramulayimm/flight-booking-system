package com.iyzico.challenge.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FlightResponseTest {

    @Test
    void noArgsConstructor_should_create_empty_object() {
        FlightResponse response = new FlightResponse();

        assertNull(response.getId());
        assertNull(response.getFlightCode());
        assertNull(response.getName());
        assertNull(response.getDescription());
    }

    @Test
    void allArgsConstructor_should_set_all_fields() {
        FlightResponse response = new FlightResponse(
                1L,
                "TK100",
                "Istanbul - Ankara",
                "Morning flight"
        );

        assertEquals(1L, response.getId());
        assertEquals("TK100", response.getFlightCode());
        assertEquals("Istanbul - Ankara", response.getName());
        assertEquals("Morning flight", response.getDescription());
    }

    @Test
    void builder_should_create_valid_object() {
        FlightResponse response = FlightResponse.builder()
                .id(2L)
                .flightCode("TK200")
                .name("Izmir - Istanbul")
                .description("Evening flight")
                .build();

        assertEquals(2L, response.getId());
        assertEquals("TK200", response.getFlightCode());
        assertEquals("Izmir - Istanbul", response.getName());
        assertEquals("Evening flight", response.getDescription());
    }

    @Test
    void setters_should_update_fields() {
        FlightResponse response = new FlightResponse();

        response.setId(3L);
        response.setFlightCode("TK300");
        response.setName("Adana - Ankara");
        response.setDescription("Test flight");

        assertEquals(3L, response.getId());
        assertEquals("TK300", response.getFlightCode());
        assertEquals("Adana - Ankara", response.getName());
        assertEquals("Test flight", response.getDescription());
    }
}
