package com.iyzico.challenge.dto;

import com.iyzico.challenge.enums.SeatStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class SeatResponse {
    private Long id;
    private Long flightId;
    private String seatNo;
    private BigDecimal price;
    private SeatStatus status;
}
