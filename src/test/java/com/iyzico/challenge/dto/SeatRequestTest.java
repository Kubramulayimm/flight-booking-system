package com.iyzico.challenge.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class SeatRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldBeValid_whenAllFieldsProvided() {
        // given
        SeatRequest request = new SeatRequest();
        request.setSeatNo("12A");
        request.setPrice(BigDecimal.valueOf(250));

        // when
        Set<ConstraintViolation<SeatRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFail_whenSeatNoIsBlank() {
        // given
        SeatRequest request = new SeatRequest();
        request.setSeatNo("   ");
        request.setPrice(BigDecimal.valueOf(250));

        // when
        Set<ConstraintViolation<SeatRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath()).hasToString("seatNo");
    }

    @Test
    void shouldFail_whenPriceIsNull() {
        // given
        SeatRequest request = new SeatRequest();
        request.setSeatNo("12A");
        request.setPrice(null);

        // when
        Set<ConstraintViolation<SeatRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath()).hasToString("price");

    }
}
