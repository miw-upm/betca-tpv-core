package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.CreditSale;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.CreditSaleResource.CREDIT_SALE;
import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.CreditSaleResource.SEARCH;
import static org.junit.jupiter.api.Assertions.*;

@RestTestConfig
public class CreditSaleResourceIT {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private RestClientTestService restClientTestService;

    @Test
    void testCreate() {
        CreditSale creditSale = CreditSale.builder().ticketReference("WB9-e8xQT4ejb74r1vLrCw").payed(true).build();
        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(CREDIT_SALE)
                .body(Mono.just(creditSale), CreditSale.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CreditSale.class)
                .value(Assertions::assertNotNull)
                .value(returnCreditSale -> {
                    assertEquals("WB9-e8xQT4ejb74r1vLrCw", returnCreditSale.getTicketReference());
                    assertEquals(true, returnCreditSale.getPayed());
                });
    }

    @Test
    void testFindByPayed() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CREDIT_SALE + SEARCH)
                        .queryParam("payed", true)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CreditSale.class)
                .value(Assertions::assertNotNull)
                .value(creditSale -> assertTrue(creditSale.stream().allMatch(CreditSale::getPayed))
                );
    }

    @Test
    void testFindByNotPayed() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CREDIT_SALE + SEARCH)
                        .queryParam("payed", false)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CreditSale.class)
                .value(Assertions::assertNotNull)
                .value(creditSale -> assertFalse(creditSale.stream().allMatch(CreditSale::getPayed))
                );
    }
}
