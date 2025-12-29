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
import com.iyzico.challenge.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService {

    private final FlightRepository flightRepository;
    private final SeatRepository seatRepository;
    private final SeatMapper seatMapper;

    @Transactional
    public SeatResponse addSeat(Long flightId, SeatRequest req) {
        Flight flight = flightRepository.findByIdAndDeletedFalse(flightId)
                .orElseThrow(() -> new NotFoundException("Flight not found: " + flightId));

        if (seatRepository.existsByFlightIdAndSeatNoAndDeletedFalse(flightId, req.getSeatNo())) {
            throw new BusinessException("Seat already exists for this flight. seatNo=" + req.getSeatNo());
        }

        Seat seat = Seat.builder()
                .flight(flight)
                .seatNo(req.getSeatNo())
                .price(req.getPrice())
                .status(SeatStatus.AVAILABLE)
                .build();

        Seat seatSave = seatRepository.save(seat);
        return seatMapper.toSeatResponse(seatSave);

    }

    @Transactional
    public SeatResponse updateSeat(Long flightId, Long seatId, SeatRequest req) {
        Seat seat = seatRepository.findByIdAndFlightIdAndDeletedFalse(seatId, flightId)
                .orElseThrow(() -> new NotFoundException("Seat not found. seatId=" + seatId + ", flightId=" + flightId));

        validateSeatUpdatable(seat);

        if (!seat.getSeatNo().equals(req.getSeatNo())
                && seatRepository.existsByFlightIdAndSeatNoAndDeletedFalse(flightId, req.getSeatNo())) {
            throw new BusinessException("Seat already exists for this flight. seatNo=" + req.getSeatNo());
        }

        seat.setSeatNo(req.getSeatNo());
        seat.setPrice(req.getPrice());
        Seat seatUpdate = seatRepository.save(seat);
        return seatMapper.toSeatResponse(seatUpdate);
    }

    @Transactional
    public void deleteSeat(Long flightId, Long seatId) {
        Seat seat = seatRepository.findByIdAndFlightIdAndDeletedFalse(seatId, flightId)
                .orElseThrow(() -> new NotFoundException("Seat not found. seatId=" + seatId + ", flightId=" + flightId));

        seat.softDelete();
        seatRepository.save(seat);
    }

    private void validateSeatUpdatable(Seat seat) {
        if (seat.getStatus() == SeatStatus.SOLD) {
            throw new BusinessException("Sold seat cannot be modified");
        }
    }
}
