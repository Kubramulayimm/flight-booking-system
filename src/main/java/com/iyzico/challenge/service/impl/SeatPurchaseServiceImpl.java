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
import com.iyzico.challenge.service.SeatPurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class SeatPurchaseServiceImpl implements SeatPurchaseService {

    private final SeatRepository seatRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentServiceClients paymentServiceClients;

    @Override
    @Transactional
    public SeatPurchaseResponse purchase(Long flightId, Long seatId, SeatPurchaseRequest req) {

        Seat reservedSeat = reserveSeatOrFail(flightId, seatId, req);

        BigDecimal finalPrice = reservedSeat.getPrice();

        try {
            paymentServiceClients.call(finalPrice).join();
        } catch (Exception e) {
            releaseSeat(flightId, seatId);
            throw new BusinessException("Payment failed: " + safeMessage(e));
        }

        Payment payment = finalizeSaleAndPersistPayment(flightId, seatId, finalPrice);

        return SeatPurchaseResponse.builder()
                .paymentId(payment.getId())
                .flightId(flightId)
                .seatId(seatId)
                .seatNo(reservedSeat.getSeatNo())
                .price(finalPrice)
                .passengerName(req.getPassengerName())
                .status("COMPLETED")
                .createdAt(new java.util.Date())
                .build();
    }

    @Transactional
    public Seat reserveSeatOrFail(Long flightId, Long seatId, SeatPurchaseRequest req) {
        Seat seat = seatRepository.findByFlightIdForUpdate(flightId, seatId)
                .orElseThrow(() -> new NotFoundException("Seat not found: seatId=" + seatId + ", flightId=" + flightId));

        if (seat.getStatus() == SeatStatus.SOLD) {
            throw new SeatNotAvailableException("Seat already sold. seatId=" + seatId);
        }
        if (seat.getStatus() == SeatStatus.RESERVED) {
            throw new SeatNotAvailableException("Seat is being purchased by another passenger. seatId=" + seatId);
        }

        BigDecimal seatPrice = seat.getPrice();
        if (seatPrice == null) {
            throw new BusinessException("Seat price is missing. seatId=" + seatId);
        }

        if (req.getPrice() != null && seatPrice.compareTo(req.getPrice()) != 0) {
            throw new BusinessException("Price mismatch. Expected=" + seatPrice + ", Given=" + req.getPrice());
        }

        seat.setStatus(SeatStatus.RESERVED);
        return seatRepository.save(seat);
    }

    @Transactional
    public void releaseSeat(Long flightId, Long seatId) {
        Seat seat = seatRepository.findByFlightIdForUpdate(flightId, seatId)
                .orElseThrow(() -> new NotFoundException("Seat not found: seatId=" + seatId + ", flightId=" + flightId));

        if (seat.getStatus() == SeatStatus.RESERVED) {
            seat.setStatus(SeatStatus.AVAILABLE);
            seatRepository.save(seat);
        }
    }

    @Transactional
    public Payment finalizeSaleAndPersistPayment(Long flightId, Long seatId, BigDecimal price) {
        Seat seat = seatRepository.findByFlightIdForUpdate(flightId, seatId)
                .orElseThrow(() -> new NotFoundException("Seat not found: seatId=" + seatId + ", flightId=" + flightId));

        if (seat.getStatus() != SeatStatus.RESERVED) {
            throw new SeatNotAvailableException("Seat cannot be finalized. Current status=" + seat.getStatus());
        }

        seat.setStatus(SeatStatus.SOLD);
        seatRepository.save(seat);

        Payment payment = Payment.builder()
                .price(price)
                .bankResponse("200")
                .build();

        return paymentRepository.save(payment);
    }

    private String safeMessage(Exception e) {
        return (e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
    }
}
