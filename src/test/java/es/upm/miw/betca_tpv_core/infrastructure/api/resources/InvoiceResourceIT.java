package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.*;
import es.upm.miw.betca_tpv_core.domain.rest.UserMicroservice;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.InvoiceItemDto;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.StockManagerDto;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.TicketBasicDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.CashierResource.CASHIERS;
import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.CashierResource.LAST;
import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.InvoiceResource.INVOICES;
import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.InvoiceResource.TICKET_REF;
import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.TicketResource.*;
import static java.math.BigDecimal.ZERO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;

@RestTestConfig
class InvoiceResourceIT {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private RestClientTestService restClientTestService;
    @MockBean
    private UserMicroservice userMicroservice;

    @BeforeEach
    void openCashier() {
        this.restClientTestService.loginAdmin(webTestClient)
                .post().uri(CASHIERS)
                .exchange()
                .expectStatus().isOk();
        BDDMockito.given(this.userMicroservice.readByMobile(anyString()))
                .willAnswer(arguments ->
                        Mono.just(User.builder().mobile(arguments.getArgument(0)).firstName("mock").build()));
    }


    @Test
    void testCreateInvoiceAndPrint() {
        TicketBasicDto ticket = TicketBasicDto.builder().reference("FGhfvfMORj6iKmzp5aERAA").build();
        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(INVOICES)
                .body(BodyInserters.fromValue(ticket))
                .exchange()
                .expectStatus().isOk()
                .expectBody(byte[].class)
                .value(Assertions::assertNotNull);
    }

    @Test
    void testCreateFromTicketRef() {
        TicketBasicDto ticket = TicketBasicDto.builder().reference("FGhfv521Rj6iKmzp5aERAA").build();
        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(INVOICES + TICKET_REF)
                .body(BodyInserters.fromValue(ticket))
                .exchange()
                .expectStatus().isOk()
                .expectBody(InvoiceItemDto.class)
                .value(Assertions::assertNotNull);
    }

    @Test
    void testfindByPhoneAndTicketIdNullSafe() {
        String ticketId = "5fa45f6f3a61083cb241289c";
        String userPhone = "666666004";

        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(INVOICES + InvoiceResource.SEARCH)
                        .queryParam("ticketId", ticketId)
                        .queryParam("userPhone", userPhone)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(InvoiceItemDto.class)
                .value(Assertions::assertNotNull)
                .value(invoices -> invoices.stream()
                        .allMatch(invoice -> ticketId.equals(invoice.getTicketId())));
    }


    @AfterEach
    void closeCashier() {
        this.restClientTestService.loginAdmin(webTestClient)
                .patch().uri(CASHIERS + LAST)
                .body(Mono.just(new CashierClose(ZERO, ZERO, "test")), CashierClose.class)
                .exchange()
                .expectStatus().isOk();
    }

}
