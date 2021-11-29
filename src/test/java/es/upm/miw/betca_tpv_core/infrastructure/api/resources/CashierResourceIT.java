package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

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
}
