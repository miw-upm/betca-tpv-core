package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Invoice;
import es.upm.miw.betca_tpv_core.domain.model.Shopping;
import es.upm.miw.betca_tpv_core.domain.model.Ticket;
import es.upm.miw.betca_tpv_core.domain.model.User;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.InvoiceResource.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RestTestConfig
public class InvoiceResourceIT {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private RestClientTestService restClientTestService;

    @Test
    void testCreate(){
        Shopping shopping1 = Shopping.builder().barcode("8400000000055").build();
        Shopping shopping2 = Shopping.builder().barcode("8400000000093").build();

        User user = User.builder()
                .mobile("666666000")
                .build();

        Ticket ticket = Ticket.builder()
                .id("5fa45e863d6e834d642689ac")
                .shoppingList(List.of(shopping1, shopping2))
                .user(user)
                .build();

        Invoice invoice = Invoice.builder()
                .user(user)
                .ticket(ticket)
                .build();

        Invoice dbInvoice = this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(INVOICES)
                .body(Mono.just(invoice), Invoice.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Invoice.class)
                .value(Assertions::assertNotNull)
                .value(returnInvoice -> {
                    assertNotNull(returnInvoice.getCreationDate());
                    assertNotNull(returnInvoice.getIdentity());
                    assertEquals(new BigDecimal("14.85"), returnInvoice.getBaseTax());
                    assertEquals(new BigDecimal("3.08"), returnInvoice.getTaxValue());
                }).returnResult().getResponseBody();
        assertNotNull(dbInvoice);
    }

    @Test
    void testCreateNotFoundArticleException() {
        Shopping shopping1 = Shopping.builder().barcode("Not Found").build();

        Ticket ticket = Ticket.builder()
                .id("3h5z6a45e863d6e834d65468hdt")
                .shoppingList(List.of(shopping1))
                .build();

        Invoice invoice = Invoice.builder()
                .ticket(ticket)
                .build();

        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(INVOICES)
                .body(Mono.just(invoice), Invoice.class)
                .exchange()
                .expectStatus().isNotFound();
    }
}