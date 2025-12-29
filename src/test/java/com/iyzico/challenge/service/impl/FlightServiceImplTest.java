package com.iyzico.challenge.service.impl;

import com.iyzico.challenge.dto.FlightRequest;
import com.iyzico.challenge.dto.FlightResponse;
import com.iyzico.challenge.entity.Flight;
import com.iyzico.challenge.exception.BusinessException;
import com.iyzico.challenge.exception.NotFoundException;
import com.iyzico.challenge.mapper.FlightMapper;
import com.iyzico.challenge.repository.FlightRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightServiceImplTest {

    @Mock FlightRepository flightRepository;
    @Mock FlightMapper flightMapper;

    @InjectMocks FlightServiceImpl flightService;

    @Test
    void create_shouldThrowIllegalArgument_whenFlightCodeNull() {
        FlightRequest req = FlightRequest.builder()
                .flightCode(null)
                .name("Name")
                .description("Desc")
                .build();

        assertThatThrownBy(() -> flightService.create(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Flight code cannot be empty");

        verifyNoInteractions(flightRepository, flightMapper);
    }

    @Test
    void create_shouldThrowIllegalArgument_whenFlightCodeBlank() {
        FlightRequest req = FlightRequest.builder()
                .flightCode("   ")
                .name("Name")
                .description("Desc")
                .build();

        assertThatThrownBy(() -> flightService.create(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Flight code cannot be empty");

        verifyNoInteractions(flightRepository, flightMapper);
    }

    @Test
    void create_shouldThrowIllegalArgument_whenNameNull() {
        FlightRequest req = FlightRequest.builder()
                .flightCode("TK1")
                .name(null)
                .description("Desc")
                .build();

        assertThatThrownBy(() -> flightService.create(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Flight name cannot be empty");

        verifyNoInteractions(flightRepository, flightMapper);
    }

    @Test
    void create_shouldThrowIllegalArgument_whenNameBlank() {
        FlightRequest req = FlightRequest.builder()
                .flightCode("TK1")
                .name("   ")
                .description("Desc")
                .build();

        assertThatThrownBy(() -> flightService.create(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Flight name cannot be empty");

        verifyNoInteractions(flightRepository, flightMapper);
    }

    @Test
    void create_shouldThrowBusinessException_whenCodeAlreadyExists() {
        FlightRequest req = FlightRequest.builder()
                .flightCode(" tk123 ")
                .name("Name")
                .description("Desc")
                .build();

        when(flightRepository.existsByFlightCodeAndDeletedFalse("TK123")).thenReturn(true);

        assertThatThrownBy(() -> flightService.create(req))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Flight code already exists: TK123");

        verify(flightRepository).existsByFlightCodeAndDeletedFalse("TK123");
        verify(flightRepository, never()).save(any());
        verifyNoInteractions(flightMapper);
    }

    @Test
    void create_shouldNormalizeCode_andTrimName_andReturnMappedResponse() {
        FlightRequest req = FlightRequest.builder()
                .flightCode(" tk123 ")
                .name("  Istanbul  ")
                .description("Desc")
                .build();

        when(flightRepository.existsByFlightCodeAndDeletedFalse("TK123")).thenReturn(false);

        ArgumentCaptor<Flight> captor = ArgumentCaptor.forClass(Flight.class);

        Flight saved = Flight.builder()
                .id(1L)
                .flightCode("TK123")
                .name("Istanbul")
                .description("Desc")
                .build();

        when(flightRepository.save(any(Flight.class))).thenReturn(saved);

        FlightResponse mapped = FlightResponse.builder()
                .id(1L)
                .flightCode("TK123")
                .name("Istanbul")
                .description("Desc")
                .build();
        when(flightMapper.toFlightResponse(saved)).thenReturn(mapped);

        FlightResponse out = flightService.create(req);

        verify(flightRepository).save(captor.capture());
        Flight toSave = captor.getValue();
        assertThat(toSave.getFlightCode()).isEqualTo("TK123");
        assertThat(toSave.getName()).isEqualTo("Istanbul");
        assertThat(toSave.getDescription()).isEqualTo("Desc");

        assertThat(out).isSameAs(mapped);
        verify(flightMapper).toFlightResponse(saved);
    }

    @Test
    void update_shouldThrowNotFound_whenFlightNotExists() {
        when(flightRepository.findByIdAndDeletedFalse(99L)).thenReturn(Optional.empty());

        FlightRequest req = FlightRequest.builder()
                .flightCode("TK1")
                .name("N")
                .description("D")
                .build();

        assertThatThrownBy(() -> flightService.update(99L, req))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Flight not found: 99");

        verify(flightRepository).findByIdAndDeletedFalse(99L);
        verify(flightRepository, never()).save(any());
        verifyNoInteractions(flightMapper);
    }

    @Test
    void update_shouldThrowBusinessException_whenNewCodeExistsAndDifferent() {
        Flight existing = Flight.builder()
                .id(1L)
                .flightCode("TK111")
                .name("Old")
                .description("OldDesc")
                .build();

        when(flightRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existing));
        when(flightRepository.existsByFlightCodeAndDeletedFalse("TK222")).thenReturn(true);

        FlightRequest req = FlightRequest.builder()
                .flightCode(" tk222 ")
                .name("New")
                .description("NewDesc")
                .build();

        assertThatThrownBy(() -> flightService.update(1L, req))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Flight code already exists: TK222");

        verify(flightRepository).existsByFlightCodeAndDeletedFalse("TK222");
        verify(flightRepository, never()).save(any());
        verifyNoInteractions(flightMapper);
    }

    @Test
    void update_shouldUpdateCodeNameDescription_whenProvided_andReturnMappedResponse() {
        Flight existing = Flight.builder()
                .id(10L)
                .flightCode("TK111")
                .name("Old")
                .description("OldDesc")
                .build();

        when(flightRepository.findByIdAndDeletedFalse(10L)).thenReturn(Optional.of(existing));
        when(flightRepository.existsByFlightCodeAndDeletedFalse("TK222")).thenReturn(false);

        FlightRequest req = FlightRequest.builder()
                .flightCode(" tk222 ")
                .name("  New Name ")
                .description("NewDesc")
                .build();

        Flight saved = Flight.builder()
                .id(10L)
                .flightCode("TK222")
                .name("New Name")
                .description("NewDesc")
                .build();

        when(flightRepository.save(any(Flight.class))).thenReturn(saved);

        FlightResponse mapped = FlightResponse.builder()
                .id(10L)
                .flightCode("TK222")
                .name("New Name")
                .description("NewDesc")
                .build();
        when(flightMapper.toFlightResponse(saved)).thenReturn(mapped);

        FlightResponse out = flightService.update(10L, req);

        assertThat(existing.getFlightCode()).isEqualTo("TK222");
        assertThat(existing.getName()).isEqualTo("New Name");
        assertThat(existing.getDescription()).isEqualTo("NewDesc");

        assertThat(out).isSameAs(mapped);
        verify(flightMapper).toFlightResponse(saved);
    }

    @Test
    void update_shouldNotChangeCode_whenBlank_andNotChangeName_whenBlank_butSetDescription() {
        Flight existing = Flight.builder()
                .id(10L)
                .flightCode("TK111")
                .name("Old")
                .description("OldDesc")
                .build();

        when(flightRepository.findByIdAndDeletedFalse(10L)).thenReturn(Optional.of(existing));

        FlightRequest req = FlightRequest.builder()
                .flightCode("   ")
                .name("  ")
                .description("OnlyDescChanged")
                .build();

        Flight saved = Flight.builder()
                .id(10L)
                .flightCode("TK111")
                .name("Old")
                .description("OnlyDescChanged")
                .build();

        when(flightRepository.save(existing)).thenReturn(saved);

        FlightResponse mapped = FlightResponse.builder()
                .id(10L)
                .flightCode("TK111")
                .name("Old")
                .description("OnlyDescChanged")
                .build();
        when(flightMapper.toFlightResponse(saved)).thenReturn(mapped);

        FlightResponse out = flightService.update(10L, req);

        assertThat(existing.getFlightCode()).isEqualTo("TK111");
        assertThat(existing.getName()).isEqualTo("Old");
        assertThat(existing.getDescription()).isEqualTo("OnlyDescChanged");
        assertThat(out).isSameAs(mapped);

        verify(flightRepository, never()).existsByFlightCodeAndDeletedFalse(anyString());
    }

    @Test
    void delete_shouldThrowNotFound_whenMissing() {
        when(flightRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> flightService.delete(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Flight not found: 1");

        verify(flightRepository, never()).save(any());
    }

    @Test
    void delete_shouldSoftDelete_andSave() {
        Flight existing = spy(Flight.builder()
                .id(1L)
                .flightCode("TK1")
                .name("N")
                .description("D")
                .build());

        when(flightRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existing));
        when(flightRepository.save(existing)).thenReturn(existing);

        flightService.delete(1L);

        verify(existing).softDelete();
        verify(flightRepository).save(existing);
    }

    @Test
    void get_shouldThrowNotFound_whenMissing() {
        when(flightRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> flightService.get(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Flight not found: 1");

        verifyNoInteractions(flightMapper);
    }

    @Test
    void get_shouldReturnMappedResponse() {
        Flight existing = Flight.builder()
                .id(1L)
                .flightCode("TK1")
                .name("N")
                .description("D")
                .build();

        when(flightRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existing));

        FlightResponse mapped = FlightResponse.builder()
                .id(1L)
                .flightCode("TK1")
                .name("N")
                .description("D")
                .build();

        when(flightMapper.toFlightResponse(existing)).thenReturn(mapped);

        FlightResponse out = flightService.get(1L);

        assertThat(out).isSameAs(mapped);
        verify(flightMapper).toFlightResponse(existing);
    }
}
