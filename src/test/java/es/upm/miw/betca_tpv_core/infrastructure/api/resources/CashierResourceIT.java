package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Cashier;
import es.upm.miw.betca_tpv_core.domain.model.CashierClose;
import es.upm.miw.betca_tpv_core.domain.model.CashierState;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.CashierLastDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.CashierResource.*;
import static java.math.BigDecimal.ZERO;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RestTestConfig
class CashierResourceIT {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private RestClientTestService restClientTestService;

    @Test
    void testFindLast() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get().uri(CASHIERS + LAST)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CashierLastDto.class)
                .value(Assertions::assertNotNull)
                .value(cashier -> assertTrue(cashier.getClosed()));
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
    void testFindAllClosedBetween(){
        this.restClientTestService.loginAdmin(webTestClient)
                .get().uri(CASHIERS + CLOSED_BETWEEN +"?from=1970-01-01&to=1970-01-01")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Cashier.class).hasSize(0);
        this.restClientTestService.loginAdmin(webTestClient)
                .get().uri(CASHIERS + CLOSED_BETWEEN +"?from=1970-01-01&to=1970-02-01")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Cashier.class).value( cashiers -> cashiers.forEach( cashier -> {
                    Assertions.assertTrue( cashier.getClosureDate().isAfter(LocalDateTime.of(1970,1,1, 0, 0)));
                    Assertions.assertTrue( cashier.getClosureDate().isBefore(LocalDateTime.of(1970,2,1, 0, 0)));
                }));
        this.restClientTestService.loginAdmin(webTestClient)
                .get().uri(CASHIERS + CLOSED_BETWEEN +"?from=1971-01-01&to=1972-01-01")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Cashier.class).value( cashiers -> cashiers.forEach( cashier -> {
                    Assertions.assertTrue( cashier.getClosureDate().isAfter(LocalDateTime.of(1971,1,1, 0, 0)));
                    Assertions.assertTrue( cashier.getClosureDate().isBefore(LocalDateTime.of(1972,1,1, 0, 0)));
                }));
    }
}
