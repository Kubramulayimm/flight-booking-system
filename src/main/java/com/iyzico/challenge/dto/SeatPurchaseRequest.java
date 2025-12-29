package com.iyzico.challenge.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class SeatPurchaseRequest {
    @NotBlank
    private String passengerName;
    @NotNull
    private BigDecimal price;
}
