package com.iyzico.challenge.service;

import com.iyzico.challenge.dto.FlightRequest;
import com.iyzico.challenge.dto.FlightResponse;


public interface FlightService {

    FlightResponse create(FlightRequest req);

    FlightResponse update(Long id, FlightRequest req);

    void delete(Long id);

    FlightResponse get(Long id);
}
