package com.iyzico.challenge.service.impl;

import com.iyzico.challenge.dto.FlightSeatListingResponse;
import com.iyzico.challenge.entity.Flight;
import com.iyzico.challenge.enums.SeatStatus;
import com.iyzico.challenge.exception.NotFoundException;
import com.iyzico.challenge.repository.FlightRepository;
import com.iyzico.challenge.repository.SeatRepository;
import com.iyzico.challenge.service.ListingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListingServiceImpl implements ListingService {

    private final FlightRepository flightRepository;
    private final SeatRepository seatRepository;

    @Transactional(readOnly = true)
    public FlightSeatListingResponse listFlightSeatsByCode(String flightCode) {
        Flight flight = flightRepository.findByFlightCodeAndDeletedFalse(flightCode)
                .orElseThrow(() -> new NotFoundException("Flight not found: " + flightCode));

        var availableSeats = seatRepository
                .findAllByFlightIdAndStatusAndDeletedFalse(flight.getId(), SeatStatus.AVAILABLE)
                .stream()
                .map(s -> FlightSeatListingResponse.AvailableSeatItem.builder()
                        .seatId(s.getId())
                        .seatNo(s.getSeatNo())
                        .price(s.getPrice())
                        .build())
                .collect(Collectors.toList());

        return FlightSeatListingResponse.builder()
                .flightId(flight.getId())
                .flightCode(flight.getFlightCode())
                .flightName(flight.getName())
                .description(flight.getDescription())
                .availableSeats(availableSeats)
                .build();
    }
}
