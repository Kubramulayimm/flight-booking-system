package com.iyzico.challenge.service;

import com.iyzico.challenge.dto.SeatRequest;
import com.iyzico.challenge.dto.SeatResponse;

public interface SeatService {
    SeatResponse addSeat(Long flightId, SeatRequest req);

    SeatResponse updateSeat(Long flightId, Long seatId, SeatRequest req);

    void deleteSeat(Long flightId, Long seatId);
}
