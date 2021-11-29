package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.*;
import es.upm.miw.betca_tpv_core.domain.rest.UserMicroservice;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.CashierResource.CASHIERS;
import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.CashierResource.LAST;
import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.TicketResource.*;
import static java.math.BigDecimal.ZERO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;

@RestTestConfig
class TicketResourceIT {

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
    void testCreate() {
        Shopping shopping1 = Shopping.builder().barcode("8400000000017").retailPrice(new BigDecimal("20")).amount(1)
                .discount(BigDecimal.ZERO).state(ShoppingState.COMMITTED).build();
        Shopping shopping2 = Shopping.builder().barcode("8400000000024").retailPrice(new BigDecimal("27.8")).amount(3)
                .discount(BigDecimal.TEN).state(ShoppingState.NOT_COMMITTED).build();
        Ticket ticket = Ticket.builder().cash(new BigDecimal("200"))
                .card(BigDecimal.ZERO).voucher(BigDecimal.ZERO).note("note")
                .shoppingList(List.of(shopping1, shopping2)).user(User.builder().mobile("666666004").build()).build();
        Ticket dbTicket = this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(TICKETS)
                .body(Mono.just(ticket), Ticket.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Ticket.class)
                .value(Assertions::assertNotNull)
                .value(returnTicket -> {
                    System.out.println(">>>>> ticket: " + returnTicket + " total: " + returnTicket.total());
                    assertNotNull(returnTicket.getId());
                    assertNotNull(returnTicket.getReference());
                    assertNotNull(returnTicket.getCreationDate());
                    assertEquals(0, new BigDecimal("95.06").compareTo(returnTicket.total()));
                }).returnResult().getResponseBody();
        assertNotNull(dbTicket);
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(TICKETS + ID_ID + RECEIPT, dbTicket.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(byte[].class)
                .value(Assertions::assertNotNull);
    }

    @Test
    void testCreateNotFoundArticleException() {
        Shopping shopping1 = Shopping.builder().barcode("kk").amount(1)
                .discount(BigDecimal.ZERO).state(ShoppingState.COMMITTED).build();
        Ticket ticket = Ticket.builder().cash(new BigDecimal("200"))
                .card(BigDecimal.ZERO).voucher(BigDecimal.ZERO).note("note")
                .shoppingList(List.of(shopping1)).build();
        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(TICKETS)
                .body(Mono.just(ticket), Ticket.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testCreateUnauthorizedException() {
        Shopping shopping1 = Shopping.builder().barcode("kk").amount(1)
                .discount(BigDecimal.ZERO).state(ShoppingState.COMMITTED).build();
        Ticket ticket = Ticket.builder().cash(new BigDecimal("200"))
                .card(BigDecimal.ZERO).voucher(BigDecimal.ZERO).note("note")
                .shoppingList(List.of(shopping1)).build();
        webTestClient
                .post()
                .uri(TICKETS)
                .body(Mono.just(ticket), Ticket.class)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void testReceipt() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(TICKETS + ID_ID + RECEIPT, "5fa45e863d6e834d642689ac")
                .exchange()
                .expectStatus().isOk()
                .expectBody(byte[].class)
                .value(Assertions::assertNotNull);
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
