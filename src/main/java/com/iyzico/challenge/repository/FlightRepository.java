package com.iyzico.challenge.repository;

import com.iyzico.challenge.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FlightRepository extends JpaRepository<Flight, Long> {
    Optional<Flight> findByIdAndDeletedFalse(Long id);

    Optional<Flight> findByFlightCodeAndDeletedFalse(String flightCode);

    boolean existsByFlightCodeAndDeletedFalse(String flightCode);

}