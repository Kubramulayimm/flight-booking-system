package com.iyzico.challenge.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightResponse {
    private Long id;
    private String flightCode;
    private String name;
    private String description;
}
