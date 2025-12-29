package com.iyzico.challenge.mapper;

import com.iyzico.challenge.dto.FlightResponse;
import com.iyzico.challenge.entity.Flight;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FlightMapper {
    FlightResponse toFlightResponse(Flight flight);
}
