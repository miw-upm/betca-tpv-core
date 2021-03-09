package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Credit;
import es.upm.miw.betca_tpv_core.domain.model.CreditSale;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.CreditResource.*;
import static org.junit.jupiter.api.Assertions.*;

@RestTestConfig
public class CreditResourceIT {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private RestClientTestService restClientTestService;

    @Test
    void testCreate() {
        Credit credit = Credit.builder().reference("gdsffgd").userReference("4344354554").build();
        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(CREDIT)
                .body(Mono.just(credit), Credit.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Credit.class)
                .value(Assertions::assertNotNull)
                .value(returnCredit -> {
                    assertEquals("gdsffgd", returnCredit.getReference());
                    assertEquals("4344354554", returnCredit.getUserReference());
                });
    }

    @Test
    void testFindByUserReference() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CREDIT + SEARCH)
                        .queryParam("userReference", "53354324")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Credit.class)
                .value(Assertions::assertNotNull)
                .value(credit -> assertEquals("sdgfsgfdg53", credit.getReference())
                );
    }

    @Test
    void testAddCreditSale() {
        CreditSale creditSale = CreditSale.builder().reference("dsfdsf54fds").ticketReference("WB9-e8xQT4ejb74r1vLrCw").payed(false).build();
        this.restClientTestService.loginAdmin(webTestClient)
                .put()
                .uri(CREDIT + USER_REF, "53354324")
                .body(Mono.just(creditSale), CreditSale.class)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testFindCreditSalesWithOnlyUnpaidTickets() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CREDIT + SEARCH_UNPAID)
                        .queryParam("userReference", "53354324")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(List.class)
                .value(Assertions::assertNotNull)
                .value(creditSalesList -> assertEquals(1, creditSalesList.size()));
    }

}
