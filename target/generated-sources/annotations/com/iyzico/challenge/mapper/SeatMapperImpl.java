package com.iyzico.challenge.mapper;

import com.iyzico.challenge.dto.SeatResponse;
import com.iyzico.challenge.entity.Flight;
import com.iyzico.challenge.entity.Seat;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-29T21:12:54+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 11.0.29 (Microsoft)"
)
@Component
public class SeatMapperImpl implements SeatMapper {

    @Override
    public SeatResponse toSeatResponse(Seat seat) {
        if ( seat == null ) {
            return null;
        }

        SeatResponse.SeatResponseBuilder seatResponse = SeatResponse.builder();

        seatResponse.flightId( seatFlightId( seat ) );
        seatResponse.id( seat.getId() );
        seatResponse.seatNo( seat.getSeatNo() );
        seatResponse.price( seat.getPrice() );
        seatResponse.status( seat.getStatus() );

        return seatResponse.build();
    }

    private Long seatFlightId(Seat seat) {
        if ( seat == null ) {
            return null;
        }
        Flight flight = seat.getFlight();
        if ( flight == null ) {
            return null;
        }
        Long id = flight.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
