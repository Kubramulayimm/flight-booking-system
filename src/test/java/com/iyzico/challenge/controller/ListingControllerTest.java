package com.iyzico.challenge.controller;

import com.iyzico.challenge.dto.FlightSeatListingResponse;
import com.iyzico.challenge.service.ListingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ListingController.class)
class ListingControllerTest {

    @Autowired MockMvc mockMvc;

    @MockBean ListingService listingService;

    @Test
    void list_shouldReturn200_andBody() throws Exception {
        String flightCode = "TK101";

        FlightSeatListingResponse resp = FlightSeatListingResponse.builder()
                .flightId(10L)
                .flightCode("TK101")
                .flightName("Istanbul - Ankara")
                .description("Morning flight")
                .availableSeats(List.of(
                        FlightSeatListingResponse.AvailableSeatItem.builder()
                                .seatId(1L).seatNo("1A").price(new BigDecimal("100.00")).build(),
                        FlightSeatListingResponse.AvailableSeatItem.builder()
                                .seatId(2L).seatNo("1B").price(new BigDecimal("120.50")).build()
                ))
                .build();

        when(listingService.listFlightSeatsByCode(flightCode)).thenReturn(resp);

        mockMvc.perform(get("/listing/flights/{flightCode}", flightCode)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flightId").value(10))
                .andExpect(jsonPath("$.flightCode").value("TK101"))
                .andExpect(jsonPath("$.flightName").value("Istanbul - Ankara"))
                .andExpect(jsonPath("$.description").value("Morning flight"))
                .andExpect(jsonPath("$.availableSeats.length()").value(2))
                .andExpect(jsonPath("$.availableSeats[0].seatId").value(1))
                .andExpect(jsonPath("$.availableSeats[0].seatNo").value("1A"))
                .andExpect(jsonPath("$.availableSeats[0].price").value(100.00))
                .andExpect(jsonPath("$.availableSeats[1].seatId").value(2))
                .andExpect(jsonPath("$.availableSeats[1].seatNo").value("1B"))
                .andExpect(jsonPath("$.availableSeats[1].price").value(120.50));

        verify(listingService).listFlightSeatsByCode(flightCode);
        verifyNoMoreInteractions(listingService);
    }
}
