package com.iyzico.challenge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iyzico.challenge.dto.SeatPurchaseRequest;
import com.iyzico.challenge.dto.SeatPurchaseResponse;
import com.iyzico.challenge.service.SeatPurchaseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SeatPurchaseController.class)
class SeatPurchaseControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean SeatPurchaseService seatPurchaseService;

    @Test
    void purchase_shouldReturn201_andBody() throws Exception {
        Long flightId = 10L;
        Long seatId = 99L;

        SeatPurchaseRequest req = new SeatPurchaseRequest();
        req.setPassengerName("Kubra");
        req.setPrice(new BigDecimal("200.00"));

        SeatPurchaseResponse resp = SeatPurchaseResponse.builder()
                .seatId(seatId)
                .price(new BigDecimal("200.00"))
                .build();

        when(seatPurchaseService.purchase(eq(flightId), eq(seatId), any(SeatPurchaseRequest.class)))
                .thenReturn(resp);

        mockMvc.perform(post("/flights/{flightId}/seats/{seatId}/purchase", flightId, seatId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.seatId").value(seatId))
                .andExpect(jsonPath("$.price").value(200.00));

        verify(seatPurchaseService).purchase(eq(flightId), eq(seatId), any(SeatPurchaseRequest.class));
        verifyNoMoreInteractions(seatPurchaseService);
    }

}
