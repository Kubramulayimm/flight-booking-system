package com.iyzico.challenge.controller;

import com.iyzico.challenge.dto.FlightRequest;
import com.iyzico.challenge.dto.FlightResponse;
import com.iyzico.challenge.service.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/flight")
public class FlightController {

    private final FlightService flightService;

    @PostMapping("/create")
    public ResponseEntity<FlightResponse> create(@Valid @RequestBody FlightRequest req) {
        FlightResponse flightResponse = flightService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(flightResponse);
    }

    @PutMapping("/update/{flightId}")
    public ResponseEntity<FlightResponse> update(@PathVariable Long flightId, @Valid @RequestBody FlightRequest req) {
        FlightResponse flightResponse = flightService.update(flightId, req);
        return ResponseEntity.ok(flightResponse);
    }

    @DeleteMapping("/delete/{flightId}")
    public ResponseEntity<Void> delete(@PathVariable Long flightId) {
        flightService.delete(flightId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{flightId}")
    public FlightResponse get(@PathVariable Long flightId) {
        return flightService.get(flightId);
    }

}
