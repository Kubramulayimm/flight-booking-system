package com.iyzico.challenge.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class SeatRequest {
    @NotBlank
    private String seatNo;
    @NotNull
    private BigDecimal price;
}
