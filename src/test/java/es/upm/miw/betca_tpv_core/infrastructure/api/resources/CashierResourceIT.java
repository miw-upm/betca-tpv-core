package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.*;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.CashierLastDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.CashierResource.*;
import static java.math.BigDecimal.ZERO;
import static org.junit.jupiter.api.Assertions.*;

@RestTestConfig
class CashierResourceIT {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private RestClientTestService restClientTestService;

    @BeforeEach
    void init() {
        System.setProperty("miw.slack.uri", "");
    }

    @Test
    void testFindLast() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get().uri(CASHIERS + LAST)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CashierLastDto.class)
                .value(Assertions::assertNotNull)
                .value(cashier -> assertNotNull(cashier.getClosed()));
    }

    @Test
    void testFindLastUnauthorizedException() {
        webTestClient
                .get().uri(CASHIERS + LAST)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void testCreateAndFindLastState() {
        this.restClientTestService.loginAdmin(webTestClient)
                .post().uri(CASHIERS)
                .exchange()
                .expectStatus().isOk();
        this.restClientTestService.loginAdmin(webTestClient)
                .get().uri(CASHIERS + LAST)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CashierLastDto.class)
                .value(Assertions::assertNotNull)
                .value(cashier -> assertFalse(cashier.getClosed()));
        this.restClientTestService.loginAdmin(webTestClient)
                .get().uri(CASHIERS + LAST + STATE)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CashierState.class)
                .value(Assertions::assertNotNull);
        this.restClientTestService.loginAdmin(webTestClient)
                .patch().uri(CASHIERS + LAST)
                .body(Mono.just(new CashierClose(BigDecimal.ZERO, BigDecimal.ZERO, "test")), CashierClose.class)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testFindLastState() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get().uri(CASHIERS + LAST + STATE)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testOpenCloseCashier() {
        CashierLastDto cashierLast = this.restClientTestService.loginAdmin(webTestClient)
                .get().uri(CASHIERS + LAST)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CashierLastDto.class)
                .value(Assertions::assertNotNull)
                .returnResult()
                .getResponseBody();
        if(!cashierLast.getClosed()) {
            this.restClientTestService.loginAdmin(webTestClient)
                    .patch().uri(CASHIERS + LAST)
                    .body(Mono.just(new CashierClose(ZERO, ZERO, "test")), CashierClose.class)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(CashierLastDto.class)
                    .value(Assertions::assertNotNull)
                    .value(cashier -> assertTrue(cashier.getClosed()));
        }
        this.restClientTestService.loginAdmin(webTestClient)
                .post().uri(CASHIERS)
                .exchange()
                .expectStatus().isOk();
        this.restClientTestService.loginAdmin(webTestClient)
                .patch().uri(CASHIERS + LAST)
                .body(Mono.just(new CashierClose(ZERO, ZERO, "test")), CashierClose.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CashierLastDto.class)
                .value(Assertions::assertNotNull)
                .value(cashier -> assertTrue(cashier.getClosed()));
    }

    @Test
    void testFindAllCashierByClosureDateBetween() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(CASHIERS + SEARCH)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Cashier.class)
                .value(Assertions::assertNotNull)
                .value(cashiers -> assertTrue(cashiers.stream()
                        .anyMatch(cashier ->
                                cashier.getOpeningDate() != null)));
    }

    @Test
    void testFindAllCashierSearch() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(CASHIERS + SEARCH)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Cashier.class)
                .value(Assertions::assertNotNull)
                .value(cashiers -> assertTrue(cashiers.stream()
                        .anyMatch(cashier ->
                                cashier.getOpeningDate() != null)));
    }

    @Test
    void testMovementInCash() {
        CashierLastDto cashierLast = this.restClientTestService.loginAdmin(webTestClient)
                .get().uri(CASHIERS + LAST)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CashierLastDto.class)
                .value(Assertions::assertNotNull)
                .returnResult()
                .getResponseBody();
        if(cashierLast.getClosed())
        {
            this.restClientTestService.loginAdmin(webTestClient)
                    .post().uri(CASHIERS)
                    .exchange()
                    .expectStatus().isOk();
        }

        CashierState cashierState = this.restClientTestService.loginAdmin(webTestClient)
                .get().uri(CASHIERS + LAST + STATE)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CashierState.class)
                .returnResult()
                .getResponseBody();

        this.restClientTestService.loginAdmin(webTestClient)
                .patch().uri(CASHIERS + LAST + MOVEMENT_IN)
                .body(Mono.just(new CashierMovement(BigDecimal.valueOf(3000))), CashierMovement.class)
                .exchange()
                .expectStatus().isOk();

        this.restClientTestService.loginAdmin(webTestClient)
                .get().uri(CASHIERS + LAST + STATE)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CashierState.class)
                .value(Assertions::assertNotNull)
                .value(cashierState2 ->
                        assertEquals(0, cashierState2.getTotalCash().compareTo(cashierState.getTotalCash().add(BigDecimal.valueOf(3000)))));
    }

    @Test
    void testMovementOutCash() {
        CashierLastDto cashierLast = this.restClientTestService.loginAdmin(webTestClient)
                .get().uri(CASHIERS + LAST)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CashierLastDto.class)
                .value(Assertions::assertNotNull)
                .returnResult()
                .getResponseBody();
        if(cashierLast.getClosed())
        {
            this.restClientTestService.loginAdmin(webTestClient)
                    .post().uri(CASHIERS)
                    .exchange()
                    .expectStatus().isOk();
        }

        CashierState cashierState = this.restClientTestService.loginAdmin(webTestClient)
                .get().uri(CASHIERS + LAST + STATE)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CashierState.class)
                .returnResult()
                .getResponseBody();

        this.restClientTestService.loginAdmin(webTestClient)
                .patch().uri(CASHIERS + LAST + MOVEMENT_OUT)
                .body(Mono.just(new CashierMovement(BigDecimal.valueOf(3000))), CashierMovement.class)
                .exchange()
                .expectStatus().isOk();

        this.restClientTestService.loginAdmin(webTestClient)
                .get().uri(CASHIERS + LAST + STATE)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CashierState.class)
                .value(Assertions::assertNotNull)
                .value(cashierState2 ->
                        assertEquals(0, cashierState2.getTotalCash().compareTo(cashierState.getTotalCash().subtract(BigDecimal.valueOf(3000)))));
    }

    @Test
    void testFindAllCashierSearchTotals() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(CASHIERS + SEARCH + TOTALS)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Cashier.class)
                .value(Assertions::assertNotNull)
                .value(cashier -> assertTrue(cashier.getFinalCash().compareTo(ZERO) >= 0));
    }


}
