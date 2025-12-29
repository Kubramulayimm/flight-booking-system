package com.iyzico.challenge.exception;

import com.iyzico.challenge.exception.GlobalExceptionHandler.ApiError;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNotFound_shouldReturn404_andProperBody() {
        // given
        NotFoundException ex = new NotFoundException("Seat not found");

        // when
        ResponseEntity<ApiError> res = handler.handleNotFound(ex);

        // then
        assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
        assertNotNull(res.getBody());

        ApiError body = res.getBody();
        assertEquals("NOT_FOUND", body.getCode());
        assertEquals("Seat not found", body.getMessage());
        assertTimestampIsRecent(body.getTimestamp());
    }

    @Test
    void handleBusiness_shouldReturn409_andProperBody() {
        // given
        BusinessException ex = new BusinessException("Flight already exists");

        // when
        ResponseEntity<ApiError> res = handler.handleBusiness(ex);

        // then
        assertEquals(HttpStatus.CONFLICT, res.getStatusCode());
        assertNotNull(res.getBody());

        ApiError body = res.getBody();
        assertEquals("BUSINESS_ERROR", body.getCode());
        assertEquals("Flight already exists", body.getMessage());
        assertTimestampIsRecent(body.getTimestamp());
    }

    @Test
    void handleSeatNotAvailable_shouldReturn409_andProperBody() {
        // given
        SeatNotAvailableException ex = new SeatNotAvailableException("Seat is already reserved");

        // when
        ResponseEntity<ApiError> res = handler.handleSeatNotAvailable(ex);

        // then
        assertEquals(HttpStatus.CONFLICT, res.getStatusCode());
        assertNotNull(res.getBody());

        ApiError body = res.getBody();
        assertEquals("SEAT_NOT_AVAILABLE", body.getCode());
        assertEquals("Seat is already reserved", body.getMessage());
        assertTimestampIsRecent(body.getTimestamp());
    }

    @Test
    void handleUnexpected_shouldReturn500_andGenericMessage() {
        // given
        Exception ex = new RuntimeException("DB down");

        // when
        ResponseEntity<ApiError> res = handler.handleUnexpected(ex);

        // then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, res.getStatusCode());
        assertNotNull(res.getBody());

        ApiError body = res.getBody();
        assertEquals("INTERNAL_ERROR", body.getCode());
        assertEquals("Unexpected error occurred", body.getMessage());
        assertTimestampIsRecent(body.getTimestamp());
    }

    private static void assertTimestampIsRecent(Instant ts) {
        assertNotNull(ts);
        Duration diff = Duration.between(ts, Instant.now()).abs();
        assertTrue(diff.compareTo(Duration.ofSeconds(5)) <= 0, "timestamp is not recent: " + ts);
    }
}
