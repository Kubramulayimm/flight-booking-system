package com.iyzico.challenge.service;

import com.iyzico.challenge.dto.SeatPurchaseRequest;
import com.iyzico.challenge.dto.SeatPurchaseResponse;

public interface SeatPurchaseService {
    SeatPurchaseResponse purchase(Long flightId, Long seatId, SeatPurchaseRequest req);
}
