package com.iyzico.challenge.integration;

import com.iyzico.challenge.dto.SeatPurchaseRequest;
import com.iyzico.challenge.entity.Flight;
import com.iyzico.challenge.entity.Seat;
import com.iyzico.challenge.enums.SeatStatus;
import com.iyzico.challenge.iyzico.IyzicoNon3dsClient;
import com.iyzico.challenge.repository.FlightRepository;
import com.iyzico.challenge.repository.SeatRepository;
import com.iyzico.challenge.service.PaymentServiceClients;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SeatPurchaseConcurrencyIT {

    @Autowired TestRestTemplate restTemplate;

    @Autowired FlightRepository flightRepository;
    @Autowired SeatRepository seatRepository;

    @MockBean PaymentServiceClients paymentServiceClients;

    private Long flightId;
    private Long seatId;

    @BeforeEach
    void setup() {
        when(paymentServiceClients.call(any(BigDecimal.class)))
                .thenAnswer(inv -> CompletableFuture.completedFuture("success"));

        Flight flight = flightRepository.save(
                Flight.builder()
                        .name("IST-ADB")
                        .description("test")
                        .flightCode("TK100")
                        .build()
        );

        Seat seat = seatRepository.save(
                Seat.builder()
                        .flight(flight)
                        .seatNo("1A")
                        .price(new BigDecimal("100.00"))
                        .status(SeatStatus.AVAILABLE)
                        .build()
        );

        this.flightId = flight.getId();
        this.seatId = seat.getId();
    }

    @Test
    void sameSeat_twoConcurrentPurchases_oneSuccess_oneConflict() throws Exception {
        String url = "/flights/" + flightId + "/seats/" + seatId + "/purchase";

        SeatPurchaseRequest req = new SeatPurchaseRequest();
        req.setPassengerName("Kubra");
        req.setPrice(new BigDecimal("100.00"));

        int threadCount = 2;
        ExecutorService pool = Executors.newFixedThreadPool(threadCount);

        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);

        Callable<ResponseEntity<String>> task = () -> {
            ready.countDown();
            start.await(2, TimeUnit.SECONDS);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<SeatPurchaseRequest> entity = new HttpEntity<>(req, headers);

            return restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        };

        Future<ResponseEntity<String>> f1 = pool.submit(task);
        Future<ResponseEntity<String>> f2 = pool.submit(task);

        assertThat(ready.await(2, TimeUnit.SECONDS)).isTrue();
        start.countDown();

        ResponseEntity<String> r1 = f1.get(5, TimeUnit.SECONDS);
        ResponseEntity<String> r2 = f2.get(5, TimeUnit.SECONDS);

        pool.shutdownNow();

        AtomicInteger created = new AtomicInteger();
        AtomicInteger conflict = new AtomicInteger();

        for (ResponseEntity<String> r : new ResponseEntity[]{r1, r2}) {
            if (r.getStatusCode() == HttpStatus.CREATED) created.incrementAndGet();
            if (r.getStatusCode() == HttpStatus.CONFLICT) conflict.incrementAndGet();
        }

        assertThat(created.get()).isEqualTo(1);
        assertThat(conflict.get()).isEqualTo(1);

        Seat latest = seatRepository.findById(seatId).orElseThrow();
        assertThat(latest.getStatus()).isEqualTo(SeatStatus.SOLD);
    }
}
