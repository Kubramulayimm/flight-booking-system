package com.iyzico.challenge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iyzico.challenge.dto.FlightRequest;
import com.iyzico.challenge.dto.FlightResponse;
import com.iyzico.challenge.service.FlightService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = FlightController.class)
class FlightControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean FlightService flightService;

    @Test
    void create_shouldReturn201_andBody() throws Exception {
        FlightRequest req = FlightRequest.builder()
                .flightCode(" tk123 ")
                .name("  Istanbul ")
                .description("desc")
                .build();

        FlightResponse resp = FlightResponse.builder()
                .id(1L)
                .flightCode("TK123")
                .name("Istanbul")
                .description("desc")
                .build();

        when(flightService.create(any(FlightRequest.class))).thenReturn(resp);

        mockMvc.perform(post("/flight/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.flightCode").value("TK123"))
                .andExpect(jsonPath("$.name").value("Istanbul"))
                .andExpect(jsonPath("$.description").value("desc"));

        verify(flightService).create(any(FlightRequest.class));
    }

    @Test
    void update_shouldReturn200_andBody() throws Exception {
        FlightRequest req = FlightRequest.builder()
                .flightCode(" tk124 ")
                .name(" Name ")
                .description("new-desc")
                .build();

        FlightResponse resp = FlightResponse.builder()
                .id(10L)
                .flightCode("TK124")
                .name("Name")
                .description("new-desc")
                .build();

        when(flightService.update(eq(10L), any(FlightRequest.class))).thenReturn(resp);

        mockMvc.perform(put("/flight/update/{flightId}", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.flightCode").value("TK124"))
                .andExpect(jsonPath("$.name").value("Name"))
                .andExpect(jsonPath("$.description").value("new-desc"));

        verify(flightService).update(eq(10L), any(FlightRequest.class));
    }

    @Test
    void delete_shouldReturn200() throws Exception {
        Mockito.doNothing().when(flightService).delete(5L);

        mockMvc.perform(delete("/flight/delete/{flightId}", 5L))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(flightService).delete(5L);
    }

    @Test
    void get_shouldReturn200_andBody() throws Exception {
        FlightResponse resp = FlightResponse.builder()
                .id(7L)
                .flightCode("TK777")
                .name("X")
                .description("D")
                .build();

        when(flightService.get(7L)).thenReturn(resp);

        mockMvc.perform(get("/flight/{flightId}", 7L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7L))
                .andExpect(jsonPath("$.flightCode").value("TK777"));

        verify(flightService).get(7L);
    }
}
