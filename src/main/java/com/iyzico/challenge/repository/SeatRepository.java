package com.iyzico.challenge.repository;

import com.iyzico.challenge.entity.Seat;
import com.iyzico.challenge.enums.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    Optional<Seat> findByIdAndFlightIdAndDeletedFalse(Long id, Long flightId);

    List<Seat> findAllByFlightIdAndStatusAndDeletedFalse(Long flightId, SeatStatus status);

    boolean existsByFlightIdAndSeatNoAndDeletedFalse(Long flightId, String seatNo);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Seat s join fetch s.flight f " +
            "where s.id = :seatId and f.id = :flightId and s.deleted = false")
    Optional<Seat> findByFlightIdForUpdate(@Param("flightId") Long flightId,
                                           @Param("seatId") Long seatId);

}