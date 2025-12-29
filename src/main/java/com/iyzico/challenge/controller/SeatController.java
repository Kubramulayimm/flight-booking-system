package com.iyzico.challenge.controller;

import com.iyzico.challenge.dto.SeatRequest;
import com.iyzico.challenge.dto.SeatResponse;
import com.iyzico.challenge.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/flights/{flightId}/seats")
public class SeatController {

    private final SeatService seatService;

    @PostMapping("/add")
    public ResponseEntity<SeatResponse> add(@PathVariable Long flightId, @Valid @RequestBody SeatRequest req) {
        SeatResponse seatResponse = seatService.addSeat(flightId, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(seatResponse);

    }

    @PutMapping("/update/{seatId}")
    public ResponseEntity<SeatResponse> update(@PathVariable Long flightId,
                                               @PathVariable Long seatId,
                                               @Valid @RequestBody SeatRequest req)
                                                {
        SeatResponse seatResponse = seatService.updateSeat(flightId, seatId, req);
        return ResponseEntity.ok(seatResponse);
    }

    @DeleteMapping("/delete/{seatId}")
    public ResponseEntity<Void> delete(@PathVariable Long flightId, @PathVariable Long seatId) {
        seatService.deleteSeat(flightId, seatId);
        return ResponseEntity.ok().build();
    }


}
