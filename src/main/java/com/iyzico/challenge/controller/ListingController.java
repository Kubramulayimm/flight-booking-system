package com.iyzico.challenge.controller;

import com.iyzico.challenge.dto.FlightSeatListingResponse;
import com.iyzico.challenge.service.ListingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/listing")
public class ListingController {

    private final ListingService listingService;

    @GetMapping("/flights/{flightCode}")
    public FlightSeatListingResponse list(@PathVariable String flightCode) {
        return listingService.listFlightSeatsByCode(flightCode);
    }
}
