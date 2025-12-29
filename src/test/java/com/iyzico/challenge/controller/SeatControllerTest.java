package com.iyzico.challenge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iyzico.challenge.dto.SeatRequest;
import com.iyzico.challenge.dto.SeatResponse;
import com.iyzico.challenge.service.SeatService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = SeatController.class)
class SeatControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean SeatService seatService;

    @Test
    void add_shouldReturn201_andBody() throws Exception {
        Long flightId = 10L;

        SeatRequest req = new SeatRequest();
        req.setSeatNo("1A");
        req.setPrice(new BigDecimal("150.00"));

        SeatResponse resp = SeatResponse.builder()
                .id(5L)
                .seatNo("1A")
                .price(new BigDecimal("150.00"))
                .build();

        when(seatService.addSeat(eq(flightId), ArgumentMatchers.any(SeatRequest.class)))
                .thenReturn(resp);

        mockMvc.perform(post("/flights/{flightId}/seats/add", flightId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.seatNo").value("1A"))
                .andExpect(jsonPath("$.price").value(150.00));

        verify(seatService, times(1)).addSeat(eq(flightId), any(SeatRequest.class));
        verifyNoMoreInteractions(seatService);
    }

    @Test
    void update_shouldReturn200_andBody() throws Exception {
        Long flightId = 10L;
        Long seatId = 99L;

        SeatRequest req = new SeatRequest();
        req.setSeatNo("2B");
        req.setPrice(new BigDecimal("200.00"));

        SeatResponse resp = SeatResponse.builder()
                .id(seatId)
                .seatNo("2B")
                .price(new BigDecimal("200.00"))
                .build();

        when(seatService.updateSeat(eq(flightId), eq(seatId), any(SeatRequest.class)))
                .thenReturn(resp);

        mockMvc.perform(put("/flights/{flightId}/seats/update/{seatId}", flightId, seatId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(99))
                .andExpect(jsonPath("$.seatNo").value("2B"))
                .andExpect(jsonPath("$.price").value(200.00));

        verify(seatService).updateSeat(eq(flightId), eq(seatId), any(SeatRequest.class));
        verifyNoMoreInteractions(seatService);
    }

    @Test
    void delete_shouldReturn200() throws Exception {
        Long flightId = 10L;
        Long seatId = 99L;

        doNothing().when(seatService).deleteSeat(flightId, seatId);

        mockMvc.perform(delete("/flights/{flightId}/seats/delete/{seatId}", flightId, seatId))
                .andExpect(status().isOk());

        verify(seatService).deleteSeat(flightId, seatId);
        verifyNoMoreInteractions(seatService);
    }
}
