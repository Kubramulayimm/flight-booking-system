package com.iyzico.challenge.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightSeatListingResponse {

    private Long flightId;
    private String flightCode;
    private String flightName;
    private String description;
    private List<AvailableSeatItem> availableSeats;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvailableSeatItem {
        private Long seatId;
        private String seatNo;
        private BigDecimal price;
    }
}
