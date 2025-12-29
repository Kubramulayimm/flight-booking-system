package com.iyzico.challenge.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightRequest {
    @NotBlank
    private String flightCode;
    @NotBlank(message = "Name cannot empty")
    private String name;
    @NotBlank(message = "Description cannot empty")
    private String description;
}