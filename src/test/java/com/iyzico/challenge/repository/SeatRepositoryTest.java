package com.iyzico.challenge.repository;

import com.iyzico.challenge.entity.Flight;
import com.iyzico.challenge.entity.Seat;
import com.iyzico.challenge.enums.SeatStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class SeatRepositoryTest {

    @Autowired SeatRepository seatRepository;
    @Autowired FlightRepository flightRepository;
    @Autowired EntityManager em;

    @Test
    void existsByFlightIdAndSeatNoAndDeletedFalse_shouldWork() {
        Flight flight = flightRepository.save(Flight.builder()
                .flightCode("TK100")
                .name("Test Flight")
                .description("desc")
                .build());

        Seat seat = seatRepository.save(Seat.builder()
                .flight(flight)
                .seatNo("1A")
                .price(new BigDecimal("10.00"))
                .status(SeatStatus.AVAILABLE)
                .build());

        assertTrue(seatRepository.existsByFlightIdAndSeatNoAndDeletedFalse(flight.getId(), "1A"));

        seat.softDelete();
        seatRepository.save(seat);

        assertFalse(seatRepository.existsByFlightIdAndSeatNoAndDeletedFalse(flight.getId(), "1A"));
    }

    @Test
    void findByIdAndFlightIdAndDeletedFalse_shouldWork() {
        Flight flight = flightRepository.save(Flight.builder()
                .flightCode("TK200")
                .name("Test Flight 2")
                .description("desc")
                .build());

        Seat seat = seatRepository.save(Seat.builder()
                .flight(flight)
                .seatNo("2B")
                .price(new BigDecimal("20.00"))
                .status(SeatStatus.AVAILABLE)
                .build());

        Optional<Seat> found = seatRepository.findByIdAndFlightIdAndDeletedFalse(seat.getId(), flight.getId());
        assertTrue(found.isPresent());
        assertEquals("2B", found.get().getSeatNo());
    }

    @Test
    void findAllByFlightIdAndStatusAndDeletedFalse_shouldWork() {
        Flight flight = flightRepository.save(Flight.builder()
                .flightCode("TK300")
                .name("Test Flight 3")
                .description("desc")
                .build());

        seatRepository.save(Seat.builder().flight(flight).seatNo("1A").price(new BigDecimal("10.00")).status(SeatStatus.AVAILABLE).build());
        seatRepository.save(Seat.builder().flight(flight).seatNo("1B").price(new BigDecimal("10.00")).status(SeatStatus.SOLD).build());
        seatRepository.save(Seat.builder().flight(flight).seatNo("1C").price(new BigDecimal("10.00")).status(SeatStatus.AVAILABLE).build());

        List<Seat> available = seatRepository.findAllByFlightIdAndStatusAndDeletedFalse(flight.getId(), SeatStatus.AVAILABLE);
        assertEquals(2, available.size());
    }

    @Test
    void findByFlightIdForUpdate_shouldFetchFlight() {
        Flight flight = flightRepository.save(Flight.builder()
                .flightCode("TK400")
                .name("Test Flight 4")
                .description("desc")
                .build());

        Seat seat = seatRepository.save(Seat.builder()
                .flight(flight)
                .seatNo("9F")
                .price(new BigDecimal("99.00"))
                .status(SeatStatus.AVAILABLE)
                .build());

        em.flush();
        em.clear();

        Optional<Seat> locked = seatRepository.findByFlightIdForUpdate(flight.getId(), seat.getId());
        assertTrue(locked.isPresent());
        assertNotNull(locked.get().getFlight());
        assertEquals("TK400", locked.get().getFlight().getFlightCode());
    }
}
