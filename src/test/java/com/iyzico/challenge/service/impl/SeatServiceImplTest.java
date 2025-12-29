package com.iyzico.challenge.service.impl;

import com.iyzico.challenge.dto.SeatRequest;
import com.iyzico.challenge.dto.SeatResponse;
import com.iyzico.challenge.entity.Flight;
import com.iyzico.challenge.entity.Seat;
import com.iyzico.challenge.enums.SeatStatus;
import com.iyzico.challenge.exception.BusinessException;
import com.iyzico.challenge.exception.NotFoundException;
import com.iyzico.challenge.mapper.SeatMapper;
import com.iyzico.challenge.repository.FlightRepository;
import com.iyzico.challenge.repository.SeatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SeatServiceImplTest {

    @Mock FlightRepository flightRepository;
    @Mock SeatRepository seatRepository;
    @Mock SeatMapper seatMapper;

    @InjectMocks SeatServiceImpl seatService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addSeat_shouldThrowNotFound_whenFlightMissing() {
        Long flightId = 1L;
        SeatRequest req = new SeatRequest();
        req.setSeatNo("1A");
        req.setPrice(new BigDecimal("100.00"));

        when(flightRepository.findByIdAndDeletedFalse(flightId)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> seatService.addSeat(flightId, req));

        assertTrue(ex.getMessage().contains("Flight not found"));
        verify(flightRepository).findByIdAndDeletedFalse(flightId);
        verifyNoInteractions(seatRepository, seatMapper);
    }

    @Test
    void addSeat_shouldThrowBusiness_whenSeatAlreadyExists() {
        Long flightId = 1L;

        Flight flight = Flight.builder().id(flightId).build();

        SeatRequest req = new SeatRequest();
        req.setSeatNo("1A");
        req.setPrice(new BigDecimal("100.00"));

        when(flightRepository.findByIdAndDeletedFalse(flightId)).thenReturn(Optional.of(flight));
        when(seatRepository.existsByFlightIdAndSeatNoAndDeletedFalse(flightId, "1A")).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> seatService.addSeat(flightId, req));

        assertTrue(ex.getMessage().contains("Seat already exists"));
        verify(seatRepository, never()).save(any());
        verifyNoInteractions(seatMapper);
    }

    @Test
    void addSeat_shouldCreateSeat_withAvailableStatus() {
        Long flightId = 1L;

        Flight flight = Flight.builder().id(flightId).build();

        SeatRequest req = new SeatRequest();
        req.setSeatNo("1A");
        req.setPrice(new BigDecimal("100.00"));

        when(flightRepository.findByIdAndDeletedFalse(flightId)).thenReturn(Optional.of(flight));
        when(seatRepository.existsByFlightIdAndSeatNoAndDeletedFalse(flightId, "1A")).thenReturn(false);

        ArgumentCaptor<Seat> seatCaptor = ArgumentCaptor.forClass(Seat.class);
        Seat saved = Seat.builder()
                .id(5L)
                .flight(flight)
                .seatNo("1A")
                .price(new BigDecimal("100.00"))
                .status(SeatStatus.AVAILABLE)
                .build();

        when(seatRepository.save(any(Seat.class))).thenReturn(saved);

        SeatResponse resp = SeatResponse.builder()
                .id(5L)
                .seatNo("1A")
                .price(new BigDecimal("100.00"))
                .build();

        when(seatMapper.toSeatResponse(saved)).thenReturn(resp);

        SeatResponse result = seatService.addSeat(flightId, req);

        assertEquals(5L, result.getId());
        assertEquals("1A", result.getSeatNo());
        assertEquals(new BigDecimal("100.00"), result.getPrice());

        verify(seatRepository).save(seatCaptor.capture());
        Seat toSave = seatCaptor.getValue();
        assertEquals(flight, toSave.getFlight());
        assertEquals("1A", toSave.getSeatNo());
        assertEquals(new BigDecimal("100.00"), toSave.getPrice());
        assertEquals(SeatStatus.AVAILABLE, toSave.getStatus());
    }

    @Test
    void updateSeat_shouldThrowNotFound_whenSeatMissing() {
        when(seatRepository.findByIdAndFlightIdAndDeletedFalse(10L, 1L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> seatService.updateSeat(1L, 10L, req("1A", "50.00")));

        assertTrue(ex.getMessage().contains("Seat not found"));
        verifyNoInteractions(seatMapper);
    }

    @Test
    void updateSeat_shouldThrowBusiness_whenSeatSold() {
        Seat seat = Seat.builder()
                .id(10L)
                .seatNo("1A")
                .price(new BigDecimal("50.00"))
                .status(SeatStatus.SOLD)
                .build();

        when(seatRepository.findByIdAndFlightIdAndDeletedFalse(10L, 1L))
                .thenReturn(Optional.of(seat));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> seatService.updateSeat(1L, 10L, req("2B", "60.00")));

        assertEquals("Sold seat cannot be modified", ex.getMessage());
        verify(seatRepository, never()).save(any());
        verifyNoInteractions(seatMapper);
    }

    @Test
    void updateSeat_shouldThrowBusiness_whenSeatNoChanged_andDuplicateExists() {
        Seat seat = Seat.builder()
                .id(10L)
                .seatNo("1A")
                .price(new BigDecimal("50.00"))
                .status(SeatStatus.AVAILABLE)
                .build();

        when(seatRepository.findByIdAndFlightIdAndDeletedFalse(10L, 1L))
                .thenReturn(Optional.of(seat));
        when(seatRepository.existsByFlightIdAndSeatNoAndDeletedFalse(1L, "2B"))
                .thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> seatService.updateSeat(1L, 10L, req("2B", "60.00")));

        assertTrue(ex.getMessage().contains("Seat already exists"));
        verify(seatRepository, never()).save(any());
        verifyNoInteractions(seatMapper);
    }

    @Test
    void updateSeat_shouldUpdateAndReturnResponse() {
        Seat seat = Seat.builder()
                .id(10L)
                .seatNo("1A")
                .price(new BigDecimal("50.00"))
                .status(SeatStatus.AVAILABLE)
                .build();

        SeatRequest req = req("2B", "60.00");

        when(seatRepository.findByIdAndFlightIdAndDeletedFalse(10L, 1L))
                .thenReturn(Optional.of(seat));
        when(seatRepository.existsByFlightIdAndSeatNoAndDeletedFalse(1L, "2B"))
                .thenReturn(false);

        Seat saved = Seat.builder()
                .id(10L)
                .seatNo("2B")
                .price(new BigDecimal("60.00"))
                .status(SeatStatus.AVAILABLE)
                .build();

        when(seatRepository.save(seat)).thenReturn(saved);

        SeatResponse resp = SeatResponse.builder()
                .id(10L)
                .seatNo("2B")
                .price(new BigDecimal("60.00"))
                .build();
        when(seatMapper.toSeatResponse(saved)).thenReturn(resp);

        SeatResponse result = seatService.updateSeat(1L, 10L, req);

        assertEquals("2B", result.getSeatNo());
        assertEquals(new BigDecimal("60.00"), result.getPrice());
        assertEquals("2B", seat.getSeatNo());
        assertEquals(new BigDecimal("60.00"), seat.getPrice());
    }

    @Test
    void deleteSeat_shouldThrowNotFound_whenMissing() {
        when(seatRepository.findByIdAndFlightIdAndDeletedFalse(10L, 1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> seatService.deleteSeat(1L, 10L));

        verify(seatRepository, never()).save(any());
    }

    @Test
    void deleteSeat_shouldSoftDeleteAndSave() {
        Seat seat = Seat.builder()
                .id(10L)
                .seatNo("1A")
                .status(SeatStatus.AVAILABLE)
                .build();

        when(seatRepository.findByIdAndFlightIdAndDeletedFalse(10L, 1L))
                .thenReturn(Optional.of(seat));
        when(seatRepository.save(seat)).thenReturn(seat);

        seatService.deleteSeat(1L, 10L);

        verify(seatRepository).save(seat);

    }

    private static SeatRequest req(String seatNo, String price) {
        SeatRequest r = new SeatRequest();
        r.setSeatNo(seatNo);
        r.setPrice(new BigDecimal(price));
        return r;
    }
}
