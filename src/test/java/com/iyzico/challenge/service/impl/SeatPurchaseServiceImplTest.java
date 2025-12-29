package com.iyzico.challenge.service.impl;

import com.iyzico.challenge.dto.SeatPurchaseRequest;
import com.iyzico.challenge.dto.SeatPurchaseResponse;
import com.iyzico.challenge.entity.Payment;
import com.iyzico.challenge.entity.Seat;
import com.iyzico.challenge.enums.SeatStatus;
import com.iyzico.challenge.exception.BusinessException;
import com.iyzico.challenge.exception.NotFoundException;
import com.iyzico.challenge.exception.SeatNotAvailableException;
import com.iyzico.challenge.repository.PaymentRepository;
import com.iyzico.challenge.repository.SeatRepository;
import com.iyzico.challenge.service.PaymentServiceClients;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class SeatPurchaseServiceImplTest {

    @Mock SeatRepository seatRepository;
    @Mock PaymentRepository paymentRepository;
    @Mock PaymentServiceClients paymentServiceClients;

    @InjectMocks SeatPurchaseServiceImpl service;

    private Long flightId;
    private Long seatId;

    @BeforeEach
    void setUp() {
        flightId = 10L;
        seatId = 99L;
    }

    @Test
    void purchase_success_shouldReservePayFinalizeAndReturnResponse() {
        // given
        Seat seat = Seat.builder()
                .id(seatId)
                .seatNo("1A")
                .price(new BigDecimal("200.00"))
                .status(SeatStatus.AVAILABLE)
                .build();

        when(seatRepository.findByFlightIdForUpdate(flightId, seatId))
                .thenReturn(Optional.of(seat))
                .thenReturn(Optional.of(seat));

        when(seatRepository.save(any(Seat.class))).thenAnswer(inv -> inv.getArgument(0));

        when(paymentServiceClients.call(new BigDecimal("200.00")))
                .thenReturn(CompletableFuture.completedFuture("success"));

        Payment savedPayment = Payment.builder()
                .id(555L)
                .price(new BigDecimal("200.00"))
                .bankResponse("200")
                .build();
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        SeatPurchaseRequest req = new SeatPurchaseRequest();
        req.setPassengerName("Kubra");
        req.setPrice(new BigDecimal("200.00"));

        // when
        SeatPurchaseResponse resp = service.purchase(flightId, seatId, req);

        // then
        assertThat(resp.getPaymentId()).isEqualTo(555L);
        assertThat(resp.getFlightId()).isEqualTo(flightId);
        assertThat(resp.getSeatId()).isEqualTo(seatId);
        assertThat(resp.getSeatNo()).isEqualTo("1A");
        assertThat(resp.getPrice()).isEqualByComparingTo("200.00");
        assertThat(resp.getPassengerName()).isEqualTo("Kubra");
        assertThat(resp.getStatus()).isEqualTo("COMPLETED");
        assertThat(resp.getCreatedAt()).isNotNull();

        assertThat(seat.getStatus()).isEqualTo(SeatStatus.SOLD);

        verify(paymentServiceClients).call(new BigDecimal("200.00"));
        verify(paymentRepository).save(argThat(p ->
                p.getPrice().compareTo(new BigDecimal("200.00")) == 0
                        && "200".equals(p.getBankResponse())
        ));

        verify(seatRepository, times(2)).findByFlightIdForUpdate(flightId, seatId);
        verify(seatRepository, atLeast(2)).save(any(Seat.class));
        verifyNoMoreInteractions(paymentServiceClients, paymentRepository);
    }

    @Test
    void purchase_whenSeatNotFound_shouldThrowNotFound() {
        when(seatRepository.findByFlightIdForUpdate(flightId, seatId))
                .thenReturn(Optional.empty());

        SeatPurchaseRequest req = new SeatPurchaseRequest();
        req.setPassengerName("Kubra");

        assertThatThrownBy(() -> service.purchase(flightId, seatId, req))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Seat not found");

        verify(seatRepository).findByFlightIdForUpdate(flightId, seatId);
        verifyNoMoreInteractions(seatRepository, paymentRepository, paymentServiceClients);
    }

    @Test
    void purchase_whenSeatAlreadySold_shouldThrowSeatNotAvailable() {
        Seat seat = Seat.builder()
                .id(seatId)
                .status(SeatStatus.SOLD)
                .price(new BigDecimal("200.00"))
                .build();

        when(seatRepository.findByFlightIdForUpdate(flightId, seatId))
                .thenReturn(Optional.of(seat));

        SeatPurchaseRequest req = new SeatPurchaseRequest();

        assertThatThrownBy(() -> service.purchase(flightId, seatId, req))
                .isInstanceOf(SeatNotAvailableException.class)
                .hasMessageContaining("already sold");

        verify(seatRepository).findByFlightIdForUpdate(flightId, seatId);
        verifyNoMoreInteractions(seatRepository, paymentRepository, paymentServiceClients);
    }

    @Test
    void purchase_whenSeatReservedByOther_shouldThrowSeatNotAvailable() {
        Seat seat = Seat.builder()
                .id(seatId)
                .status(SeatStatus.RESERVED)
                .price(new BigDecimal("200.00"))
                .build();

        when(seatRepository.findByFlightIdForUpdate(flightId, seatId))
                .thenReturn(Optional.of(seat));

        SeatPurchaseRequest req = new SeatPurchaseRequest();

        assertThatThrownBy(() -> service.purchase(flightId, seatId, req))
                .isInstanceOf(SeatNotAvailableException.class)
                .hasMessageContaining("being purchased");

        verify(seatRepository).findByFlightIdForUpdate(flightId, seatId);
        verifyNoMoreInteractions(seatRepository, paymentRepository, paymentServiceClients);
    }

    @Test
    void purchase_whenSeatPriceMissing_shouldThrowBusinessException() {
        Seat seat = Seat.builder()
                .id(seatId)
                .status(SeatStatus.AVAILABLE)
                .price(null)
                .build();

        when(seatRepository.findByFlightIdForUpdate(flightId, seatId))
                .thenReturn(Optional.of(seat));

        SeatPurchaseRequest req = new SeatPurchaseRequest();

        assertThatThrownBy(() -> service.purchase(flightId, seatId, req))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Seat price is missing");

        verify(seatRepository).findByFlightIdForUpdate(flightId, seatId);
        verifyNoMoreInteractions(seatRepository, paymentRepository, paymentServiceClients);
    }

    @Test
    void purchase_whenPriceMismatch_shouldThrowBusinessException() {
        Seat seat = Seat.builder()
                .id(seatId)
                .status(SeatStatus.AVAILABLE)
                .price(new BigDecimal("200.00"))
                .build();

        when(seatRepository.findByFlightIdForUpdate(flightId, seatId))
                .thenReturn(Optional.of(seat));

        SeatPurchaseRequest req = new SeatPurchaseRequest();
        req.setPrice(new BigDecimal("199.99"));

        assertThatThrownBy(() -> service.purchase(flightId, seatId, req))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Price mismatch");

        verify(seatRepository).findByFlightIdForUpdate(flightId, seatId);
        verifyNoMoreInteractions(seatRepository, paymentRepository, paymentServiceClients);
    }

    @Test
    void purchase_whenPaymentFails_shouldReleaseSeatAndThrowBusinessException() {
        Seat seat = Seat.builder()
                .id(seatId)
                .seatNo("1A")
                .price(new BigDecimal("200.00"))
                .status(SeatStatus.AVAILABLE)
                .build();


        when(seatRepository.findByFlightIdForUpdate(flightId, seatId))
                .thenReturn(Optional.of(seat))
                .thenReturn(Optional.of(seat));

        when(seatRepository.save(any(Seat.class))).thenAnswer(inv -> inv.getArgument(0));

        CompletableFuture<String> failed = new CompletableFuture<>();
        failed.completeExceptionally(new RuntimeException("iyzico down"));
        when(paymentServiceClients.call(new BigDecimal("200.00"))).thenReturn(failed);

        SeatPurchaseRequest req = new SeatPurchaseRequest();
        req.setPassengerName("Kubra");

        assertThatThrownBy(() -> service.purchase(flightId, seatId, req))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Payment failed");

        assertThat(seat.getStatus()).isEqualTo(SeatStatus.AVAILABLE);

        verify(paymentServiceClients).call(new BigDecimal("200.00"));
        verify(paymentRepository, never()).save(any());
        verify(seatRepository, times(2)).findByFlightIdForUpdate(flightId, seatId);
        verify(seatRepository, atLeast(2)).save(any(Seat.class));
        verifyNoMoreInteractions(paymentRepository);
    }

    @Test
    void reserveSeatOrFail_success_shouldMarkReservedAndSave() {
        Seat seat = Seat.builder()
                .id(seatId)
                .seatNo("1A")
                .price(new BigDecimal("200.00"))
                .status(SeatStatus.AVAILABLE)
                .build();

        when(seatRepository.findByFlightIdForUpdate(flightId, seatId)).thenReturn(Optional.of(seat));
        when(seatRepository.save(any(Seat.class))).thenAnswer(inv -> inv.getArgument(0));

        SeatPurchaseRequest req = new SeatPurchaseRequest();
        req.setPassengerName("Kubra");

        Seat reserved = service.reserveSeatOrFail(flightId, seatId, req);

        assertThat(reserved.getStatus()).isEqualTo(SeatStatus.RESERVED);
        verify(seatRepository).findByFlightIdForUpdate(flightId, seatId);
        verify(seatRepository).save(argThat(s -> s.getStatus() == SeatStatus.RESERVED));
        verifyNoMoreInteractions(paymentRepository, paymentServiceClients);
    }

    @Test
    void releaseSeat_whenSeatReserved_shouldSetAvailableAndSave() {
        Seat seat = Seat.builder()
                .id(seatId)
                .status(SeatStatus.RESERVED)
                .price(new BigDecimal("200.00"))
                .build();

        when(seatRepository.findByFlightIdForUpdate(flightId, seatId)).thenReturn(Optional.of(seat));
        when(seatRepository.save(any(Seat.class))).thenAnswer(inv -> inv.getArgument(0));

        service.releaseSeat(flightId, seatId);

        assertThat(seat.getStatus()).isEqualTo(SeatStatus.AVAILABLE);
        verify(seatRepository).findByFlightIdForUpdate(flightId, seatId);
        verify(seatRepository).save(argThat(s -> s.getStatus() == SeatStatus.AVAILABLE));
        verifyNoMoreInteractions(paymentRepository, paymentServiceClients);
    }

    @Test
    void releaseSeat_whenSeatNotReserved_shouldNotSave() {
        Seat seat = Seat.builder()
                .id(seatId)
                .status(SeatStatus.AVAILABLE)
                .price(new BigDecimal("200.00"))
                .build();

        when(seatRepository.findByFlightIdForUpdate(flightId, seatId)).thenReturn(Optional.of(seat));

        service.releaseSeat(flightId, seatId);

        verify(seatRepository).findByFlightIdForUpdate(flightId, seatId);
        verify(seatRepository, never()).save(any());
        verifyNoMoreInteractions(paymentRepository, paymentServiceClients);
    }

    @Test
    void releaseSeat_whenSeatNotFound_shouldThrowNotFound() {
        when(seatRepository.findByFlightIdForUpdate(flightId, seatId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.releaseSeat(flightId, seatId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Seat not found");

        verify(seatRepository).findByFlightIdForUpdate(flightId, seatId);
        verifyNoMoreInteractions(paymentRepository, paymentServiceClients);
    }

    @Test
    void finalizeSaleAndPersistPayment_success_shouldSetSoldAndSavePayment() {
        Seat seat = Seat.builder()
                .id(seatId)
                .status(SeatStatus.RESERVED)
                .price(new BigDecimal("200.00"))
                .build();

        when(seatRepository.findByFlightIdForUpdate(flightId, seatId)).thenReturn(Optional.of(seat));
        when(seatRepository.save(any(Seat.class))).thenAnswer(inv -> inv.getArgument(0));

        Payment savedPayment = Payment.builder()
                .id(777L)
                .price(new BigDecimal("200.00"))
                .bankResponse("200")
                .build();
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        Payment payment = service.finalizeSaleAndPersistPayment(flightId, seatId, new BigDecimal("200.00"));

        assertThat(seat.getStatus()).isEqualTo(SeatStatus.SOLD);
        assertThat(payment.getId()).isEqualTo(777L);

        verify(seatRepository).findByFlightIdForUpdate(flightId, seatId);
        verify(seatRepository).save(argThat(s -> s.getStatus() == SeatStatus.SOLD));
        verify(paymentRepository).save(argThat(p ->
                p.getPrice().compareTo(new BigDecimal("200.00")) == 0 && "200".equals(p.getBankResponse())
        ));
        verifyNoMoreInteractions(paymentServiceClients);
    }

    @Test
    void finalizeSaleAndPersistPayment_whenSeatNotReserved_shouldThrowSeatNotAvailable() {
        Seat seat = Seat.builder()
                .id(seatId)
                .status(SeatStatus.AVAILABLE)
                .price(new BigDecimal("200.00"))
                .build();

        when(seatRepository.findByFlightIdForUpdate(flightId, seatId)).thenReturn(Optional.of(seat));

        assertThatThrownBy(() -> service.finalizeSaleAndPersistPayment(flightId, seatId, new BigDecimal("200.00")))
                .isInstanceOf(SeatNotAvailableException.class)
                .hasMessageContaining("Seat cannot be finalized");

        verify(seatRepository).findByFlightIdForUpdate(flightId, seatId);
        verify(seatRepository, never()).save(any());
        verify(paymentRepository, never()).save(any());
        verifyNoMoreInteractions(paymentServiceClients);
    }

    @Test
    void finalizeSaleAndPersistPayment_whenSeatNotFound_shouldThrowNotFound() {
        when(seatRepository.findByFlightIdForUpdate(flightId, seatId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.finalizeSaleAndPersistPayment(flightId, seatId, new BigDecimal("200.00")))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Seat not found");

        verify(seatRepository).findByFlightIdForUpdate(flightId, seatId);
        verifyNoMoreInteractions(paymentRepository, paymentServiceClients);
    }

    @Test
    void purchase_whenPaymentClientThrowsImmediately_shouldReleaseSeat_andUseMessageWithoutCause() {
        Seat seat = Seat.builder()
                .id(seatId)
                .seatNo("1A")
                .price(new BigDecimal("200.00"))
                .status(SeatStatus.AVAILABLE)
                .build();

        when(seatRepository.findByFlightIdForUpdate(flightId, seatId))
                .thenReturn(Optional.of(seat))
                .thenReturn(Optional.of(seat));

        when(seatRepository.save(any(Seat.class))).thenAnswer(inv -> inv.getArgument(0));

        when(paymentServiceClients.call(new BigDecimal("200.00")))
                .thenThrow(new RuntimeException("boom"));

        SeatPurchaseRequest req = new SeatPurchaseRequest();
        req.setPassengerName("Kubra");

        assertThatThrownBy(() -> service.purchase(flightId, seatId, req))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Payment failed")
                .hasMessageContaining("boom");

        assertThat(seat.getStatus()).isEqualTo(SeatStatus.AVAILABLE);

        verify(paymentServiceClients).call(new BigDecimal("200.00"));
        verify(paymentRepository, never()).save(any());
        verify(seatRepository, times(2)).findByFlightIdForUpdate(flightId, seatId);
        verify(seatRepository, atLeast(2)).save(any(Seat.class));
    }

}
