package com.iyzico.challenge.mapper;

import com.iyzico.challenge.dto.FlightResponse;
import com.iyzico.challenge.entity.Flight;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-29T21:12:54+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 11.0.29 (Microsoft)"
)
@Component
public class FlightMapperImpl implements FlightMapper {

    @Override
    public FlightResponse toFlightResponse(Flight flight) {
        if ( flight == null ) {
            return null;
        }

        FlightResponse.FlightResponseBuilder flightResponse = FlightResponse.builder();

        flightResponse.id( flight.getId() );
        flightResponse.flightCode( flight.getFlightCode() );
        flightResponse.name( flight.getName() );
        flightResponse.description( flight.getDescription() );

        return flightResponse.build();
    }
}
