package com.iyzico.challenge.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FlightRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void noArgsConstructor_should_create_empty_object() {
        FlightRequest req = new FlightRequest();

        assertNull(req.getFlightCode());
        assertNull(req.getName());
        assertNull(req.getDescription());
    }

    @Test
    void allArgsConstructor_should_set_all_fields() {
        FlightRequest req = new FlightRequest(
                "TK101",
                "Istanbul - Ankara",
                "Morning flight"
        );

        assertEquals("TK101", req.getFlightCode());
        assertEquals("Istanbul - Ankara", req.getName());
        assertEquals("Morning flight", req.getDescription());
    }

    @Test
    void builder_should_create_valid_object() {
        FlightRequest req = FlightRequest.builder()
                .flightCode("TK202")
                .name("Izmir - Istanbul")
                .description("Evening flight")
                .build();

        assertEquals("TK202", req.getFlightCode());
        assertEquals("Izmir - Istanbul", req.getName());
        assertEquals("Evening flight", req.getDescription());
    }

    @Test
    void setter_should_update_fields() {
        FlightRequest req = new FlightRequest();

        req.setFlightCode("TK303");
        req.setName("Adana - Ankara");
        req.setDescription("Test flight");

        assertEquals("TK303", req.getFlightCode());
        assertEquals("Adana - Ankara", req.getName());
        assertEquals("Test flight", req.getDescription());
    }

    @Test
    void validation_should_fail_when_flightCode_is_blank() {
        FlightRequest req = FlightRequest.builder()
                .flightCode("")
                .name("Name")
                .description("Desc")
                .build();

        Set<ConstraintViolation<FlightRequest>> violations = validator.validate(req);

        assertFalse(violations.isEmpty());
    }

    @Test
    void validation_should_fail_when_name_is_blank() {
        FlightRequest req = FlightRequest.builder()
                .flightCode("TK404")
                .name("")
                .description("Desc")
                .build();

        Set<ConstraintViolation<FlightRequest>> violations = validator.validate(req);

        assertTrue(
                violations.stream()
                        .anyMatch(v -> v.getMessage().equals("Name cannot empty"))
        );
    }

    @Test
    void validation_should_fail_when_description_is_blank() {
        FlightRequest req = FlightRequest.builder()
                .flightCode("TK505")
                .name("Name")
                .description("")
                .build();

        Set<ConstraintViolation<FlightRequest>> violations = validator.validate(req);

        assertTrue(
                violations.stream()
                        .anyMatch(v -> v.getMessage().equals("Description cannot empty"))
        );
    }

    @Test
    void validation_should_pass_when_all_fields_are_valid() {
        FlightRequest req = FlightRequest.builder()
                .flightCode("TK606")
                .name("Valid name")
                .description("Valid description")
                .build();

        Set<ConstraintViolation<FlightRequest>> violations = validator.validate(req);

        assertTrue(violations.isEmpty());
    }
}
