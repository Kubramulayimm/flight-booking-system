package com.iyzico.challenge.controller;

import com.iyzico.challenge.dto.SeatPurchaseRequest;
import com.iyzico.challenge.dto.SeatPurchaseResponse;
import com.iyzico.challenge.service.SeatPurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/flights/{flightId}/seats")
public class SeatPurchaseController {

    private final SeatPurchaseService seatPurchaseService;

    @PostMapping("/{seatId}/purchase")
    @ResponseStatus(HttpStatus.CREATED)
    public SeatPurchaseResponse purchase(
            @PathVariable Long flightId,
            @PathVariable Long seatId,
            @Valid @RequestBody SeatPurchaseRequest req
    ) {
        return seatPurchaseService.purchase(flightId, seatId, req);
    }
}
