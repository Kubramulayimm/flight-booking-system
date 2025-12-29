package com.iyzico.challenge.service;

import com.iyzico.challenge.dto.FlightSeatListingResponse;

public interface ListingService {
    FlightSeatListingResponse listFlightSeatsByCode(String flightCode);
}
