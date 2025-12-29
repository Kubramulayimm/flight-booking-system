package com.iyzico.challenge.service.impl;

import com.iyzico.challenge.dto.FlightSeatListingResponse;
import com.iyzico.challenge.entity.Flight;
import com.iyzico.challenge.entity.Seat;
import com.iyzico.challenge.enums.SeatStatus;
import com.iyzico.challenge.exception.NotFoundException;
import com.iyzico.challenge.repository.FlightRepository;
import com.iyzico.challenge.repository.SeatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ListingServiceImplTest {

    private FlightRepository flightRepository;
    private SeatRepository seatRepository;

    private ListingServiceImpl listingService;

    @BeforeEach
    void setUp() {
        flightRepository = mock(FlightRepository.class);
        seatRepository = mock(SeatRepository.class);
        listingService = new ListingServiceImpl(flightRepository, seatRepository);
    }

    @Test
    void listFlightSeatsByCode_shouldReturnResponse_withAvailableSeats() {
        String flightCode = "TK101";

        Flight flight = Flight.builder()
                .id(10L)
                .flightCode("TK101")
                .name("Istanbul - Ankara")
                .description("Morning flight")
                .build();

        Seat s1 = Seat.builder()
                .id(1L)
                .seatNo("1A")
                .price(new BigDecimal("100.00"))
                .status(SeatStatus.AVAILABLE)
                .flight(flight)
                .build();

        Seat s2 = Seat.builder()
                .id(2L)
                .seatNo("1B")
                .price(new BigDecimal("120.50"))
                .status(SeatStatus.AVAILABLE)
                .flight(flight)
                .build();

        when(flightRepository.findByFlightCodeAndDeletedFalse(eq(flightCode)))
                .thenReturn(Optional.of(flight));

        when(seatRepository.findAllByFlightIdAndStatusAndDeletedFalse(eq(10L), eq(SeatStatus.AVAILABLE)))
                .thenReturn(List.of(s1, s2));

        FlightSeatListingResponse resp = listingService.listFlightSeatsByCode(flightCode);

        assertNotNull(resp);
        assertEquals(10L, resp.getFlightId());
        assertEquals("TK101", resp.getFlightCode());
        assertEquals("Istanbul - Ankara", resp.getFlightName());
        assertEquals("Morning flight", resp.getDescription());

        assertNotNull(resp.getAvailableSeats());
        assertEquals(2, resp.getAvailableSeats().size());

        var item1 = resp.getAvailableSeats().get(0);
        assertEquals(1L, item1.getSeatId());
        assertEquals("1A", item1.getSeatNo());
        assertEquals(new BigDecimal("100.00"), item1.getPrice());

        var item2 = resp.getAvailableSeats().get(1);
        assertEquals(2L, item2.getSeatId());
        assertEquals("1B", item2.getSeatNo());
        assertEquals(new BigDecimal("120.50"), item2.getPrice());

        verify(flightRepository).findByFlightCodeAndDeletedFalse(eq(flightCode));
        verify(seatRepository).findAllByFlightIdAndStatusAndDeletedFalse(eq(10L), eq(SeatStatus.AVAILABLE));
        verifyNoMoreInteractions(flightRepository, seatRepository);
    }

    @Test
    void listFlightSeatsByCode_whenFlightNotFound_shouldThrow() {
        String flightCode = "NOTEXIST";

        when(flightRepository.findByFlightCodeAndDeletedFalse(eq(flightCode)))
                .thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> listingService.listFlightSeatsByCode(flightCode));

        assertTrue(ex.getMessage().contains("Flight not found: " + flightCode));

        verify(flightRepository).findByFlightCodeAndDeletedFalse(eq(flightCode));
        verifyNoInteractions(seatRepository);
        verifyNoMoreInteractions(flightRepository);
    }

    @Test
    void listFlightSeatsByCode_whenNoAvailableSeats_shouldReturnEmptyList() {
        String flightCode = "TK202";

        Flight flight = Flight.builder()
                .id(77L)
                .flightCode("TK202")
                .name("Izmir - Istanbul")
                .description("Evening flight")
                .build();

        when(flightRepository.findByFlightCodeAndDeletedFalse(eq(flightCode)))
                .thenReturn(Optional.of(flight));

        when(seatRepository.findAllByFlightIdAndStatusAndDeletedFalse(eq(77L), eq(SeatStatus.AVAILABLE)))
                .thenReturn(List.of());

        FlightSeatListingResponse resp = listingService.listFlightSeatsByCode(flightCode);

        assertNotNull(resp);
        assertEquals(77L, resp.getFlightId());
        assertEquals("TK202", resp.getFlightCode());
        assertNotNull(resp.getAvailableSeats());
        assertEquals(0, resp.getAvailableSeats().size());

        verify(flightRepository).findByFlightCodeAndDeletedFalse(eq(flightCode));
        verify(seatRepository).findAllByFlightIdAndStatusAndDeletedFalse(eq(77L), eq(SeatStatus.AVAILABLE));
        verifyNoMoreInteractions(flightRepository, seatRepository);
    }
}
