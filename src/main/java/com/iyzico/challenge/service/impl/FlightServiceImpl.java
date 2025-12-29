package com.iyzico.challenge.service.impl;

import com.iyzico.challenge.dto.FlightRequest;
import com.iyzico.challenge.dto.FlightResponse;
import com.iyzico.challenge.entity.Flight;
import com.iyzico.challenge.exception.BusinessException;
import com.iyzico.challenge.exception.NotFoundException;
import com.iyzico.challenge.mapper.FlightMapper;
import com.iyzico.challenge.repository.FlightRepository;
import com.iyzico.challenge.service.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;
    private final FlightMapper flightMapper;

    @Transactional
    public FlightResponse create(FlightRequest req) {


        if (req.getFlightCode() == null || req.getFlightCode().isBlank()) {
            throw new IllegalArgumentException("Flight code cannot be empty");
        }
        if (req.getName() == null || req.getName().isBlank()) {
            throw new IllegalArgumentException("Flight name cannot be empty");
        }

        String normalizedCode = req.getFlightCode().trim().toUpperCase();

        if (flightRepository.existsByFlightCodeAndDeletedFalse(normalizedCode)) {
            throw new BusinessException("Flight code already exists: " + normalizedCode);
        }

        Flight flight = Flight.builder()
                .flightCode(normalizedCode)
                .name(req.getName().trim())
                .description(req.getDescription())
                .build();

        Flight flightSave = flightRepository.save(flight);
        return flightMapper.toFlightResponse(flightSave);

    }

    @Transactional
    public FlightResponse update(Long flightId, FlightRequest req) {

        Flight flight = flightRepository.findByIdAndDeletedFalse(flightId)
                .orElseThrow(() -> new NotFoundException("Flight not found: " + flightId));

        if (req.getFlightCode() != null && !req.getFlightCode().isBlank()) {

            String newCode = req.getFlightCode().trim().toUpperCase();

            if (!newCode.equals(flight.getFlightCode())
                    && flightRepository.existsByFlightCodeAndDeletedFalse(newCode)) {
                throw new BusinessException("Flight code already exists: " + newCode);
            }

            flight.setFlightCode(newCode);
        }

        if (req.getName() != null && !req.getName().isBlank()) {
            flight.setName(req.getName().trim());
        }

        flight.setDescription(req.getDescription());

        Flight updated = flightRepository.save(flight);
        return flightMapper.toFlightResponse(updated);
    }


    @Transactional
    public void delete(Long flightId) {
        Flight flight = flightRepository.findByIdAndDeletedFalse(flightId)
                .orElseThrow(() -> new NotFoundException("Flight not found: " + flightId));
        flight.softDelete();
        flightRepository.save(flight);
    }

    @Transactional(readOnly = true)
    public FlightResponse get(Long flightId) {
        Flight flight = flightRepository.findByIdAndDeletedFalse(flightId)
                .orElseThrow(() -> new NotFoundException("Flight not found: " + flightId));
        return flightMapper.toFlightResponse(flight);
    }
}