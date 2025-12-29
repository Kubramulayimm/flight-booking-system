package com.iyzico.challenge.repository;

import com.iyzico.challenge.entity.Flight;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class FlightRepositoryTest {

    @Autowired FlightRepository flightRepository;

    @Test
    void repositoryMethods_shouldRespectDeletedFalse() {
        Flight active = Flight.builder()
                .flightCode("TK100")
                .name("Active")
                .description("A")
                .build();

        Flight deleted = Flight.builder()
                .flightCode("TK200")
                .name("Deleted")
                .description("D")
                .build();

        active = flightRepository.save(active);
        deleted = flightRepository.save(deleted);

        setDeletedFlag(deleted, true);
        flightRepository.save(deleted);

        assertThat(flightRepository.findByIdAndDeletedFalse(active.getId())).isPresent();
        assertThat(flightRepository.findByIdAndDeletedFalse(deleted.getId())).isEmpty();

        assertThat(flightRepository.findByFlightCodeAndDeletedFalse("TK100")).isPresent();
        assertThat(flightRepository.findByFlightCodeAndDeletedFalse("TK200")).isEmpty();

        assertThat(flightRepository.existsByFlightCodeAndDeletedFalse("TK100")).isTrue();
        assertThat(flightRepository.existsByFlightCodeAndDeletedFalse("TK200")).isFalse();
    }

    private static void setDeletedFlag(Object entity, boolean value) {
        Class<?> c = entity.getClass();
        while (c != null) {
            try {
                Field f = c.getDeclaredField("deleted");
                f.setAccessible(true);
                f.set(entity, value);
                return;
            } catch (NoSuchFieldException ignored) {
                c = c.getSuperclass();
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        throw new IllegalStateException("Could not find field 'deleted' in entity hierarchy.");
    }
}
