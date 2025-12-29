package com.iyzico.challenge.dto;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SeatPurchaseRequestTest {

    private final Validator validator;

    public SeatPurchaseRequestTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    void should_create_valid_request() {
        SeatPurchaseRequest req = new SeatPurchaseRequest();
        req.setPassengerName("John Doe");
        req.setPrice(new BigDecimal("150.00"));

        Set<ConstraintViolation<SeatPurchaseRequest>> violations = validator.validate(req);

        assertTrue(violations.isEmpty());
        assertEquals("John Doe", req.getPassengerName());
        assertEquals(new BigDecimal("150.00"), req.getPrice());
    }

    @Test
    void should_fail_when_passengerName_is_blank() {
        SeatPurchaseRequest req = new SeatPurchaseRequest();
        req.setPassengerName("");
        req.setPrice(new BigDecimal("100.00"));

        Set<ConstraintViolation<SeatPurchaseRequest>> violations = validator.validate(req);

        assertFalse(violations.isEmpty());
        assertTrue(
                violations.stream()
                        .anyMatch(v -> v.getPropertyPath().toString().equals("passengerName"))
        );
    }

    @Test
    void should_fail_when_price_is_null() {
        SeatPurchaseRequest req = new SeatPurchaseRequest();
        req.setPassengerName("Jane Doe");
        req.setPrice(null);

        Set<ConstraintViolation<SeatPurchaseRequest>> violations = validator.validate(req);

        assertFalse(violations.isEmpty());
        assertTrue(
                violations.stream()
                        .anyMatch(v -> v.getPropertyPath().toString().equals("price"))
        );
    }
}
