package com.iyzico.challenge.mapper;

import com.iyzico.challenge.dto.SeatResponse;
import com.iyzico.challenge.entity.Seat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SeatMapper {

    @Mapping(target = "flightId", source = "flight.id")
    SeatResponse toSeatResponse(Seat seat);

}
