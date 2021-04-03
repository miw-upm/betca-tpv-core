package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.*;
import es.upm.miw.betca_tpv_core.domain.rest.UserMicroservice;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.*;
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
import java.util.ArrayList;
import java.util.List;

import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.CashierResource.CASHIERS;
import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.CashierResource.LAST;
import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.TicketResource.*;
import static java.math.BigDecimal.ZERO;
import static org.junit.jupiter.api.Assertions.*;
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
        System.setProperty("miw.slack.uri", "");

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

    @Test
    void testFindByIdOrReferenceLikeOrUserMobileLikeNullSafe() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TICKETS + TicketResource.SEARCH)
                        .queryParam("key", "5fa45f6f3a61083cb241289c")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TicketBasicDto.class)
                .value(tickets -> assertTrue(tickets
                        .stream().anyMatch(ticket -> ticket.getId().equals("5fa45f6f3a61083cb241289c"))));
    }

    @Test
    void testFindByIdOrReferenceLikeOrUserMobileLikeNullSafeUnauthorizedException() {
        this.webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TICKETS + TicketResource.SEARCH)
                        .queryParam("key", "")
                        .build())
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void testFindById(){
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(TICKETS + ID_ID, "5fa45e863d6e834d642689ac")
                .exchange()
                .expectStatus().isOk()
                .expectBody(TicketEditionDto.class)
                .value(Assertions::assertNotNull)
                .value(ticket -> {
                    assertEquals("5fa45e863d6e834d642689ac", ticket.getId());
                });
    }

    @Test
    void testFindByIdNotFoundException(){
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(TICKETS + ID_ID, "kk")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testFindByIdUnauthorizedException() {
        this.webTestClient
                .get()
                .uri(TICKETS + ID_ID, "kk")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void testFindByReference() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(TICKETS + REFERENCE_ID + REFERENCE, "nUs81zZ4R_iuoq0_zCRm6A")
                .exchange()
                .expectStatus().isOk()
                .expectBody(TicketEditionDto.class)
                .value(Assertions::assertNotNull)
                .value(ticket -> System.out.println(">>>>> ticket: " + ticket));

    }

    @Test
    void testFindSelectedByReference() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(TICKETS + REFERENCE_ID + REFERENCE + SELECTED, "WB9-e8xQT4ejb74r1vLrCw")
                .exchange()
                .expectStatus().isOk()
                .expectBody(TicketSelectedDto.class)
                .value(Assertions::assertNotNull)
                .value(ticket -> System.out.println(">>>>> ticket: " + ticket));

    }

    @Test
    void testFindByReferenceNotFoundException() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(TICKETS + REFERENCE_ID + REFERENCE, "lel")
                .exchange()
                .expectStatus().isNotFound();

    }

    @Test
    void testGetUsersByBarcodeAndAmount() {
        List<Tracking> data = new ArrayList<>();
        Tracking tracking = new Tracking();
        tracking.setBarcode("8400000000024");
        tracking.setAmount(5);
        data.add(tracking);
        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(TICKETS + TicketResource.SEARCH + TRACKING)
                .bodyValue(data)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserBasicDto.class)
                .value(users -> System.out.println(">>>>> users: " + users));
    }

    @Test
    void testUpdateByBarcodeAndAmount() {
        List<Tracking> data = new ArrayList<>();
        Tracking tracking = new Tracking();
        tracking.setBarcode("8400000000024");
        tracking.setAmount(5);
        data.add(tracking);
        this.restClientTestService.loginAdmin(webTestClient)
                .patch()
                .uri(uriBuilder -> uriBuilder
                        .path(TICKETS + TRACKING)
                        .queryParam("state", ShoppingState.COMMITTED)
                        .build()
                )
                .bodyValue(data)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Ticket.class)
                .value(tickets -> System.out.println(">>>>> tickets: " + tickets));
    }

    @Test
    void testFindByIdAndUpdate() {
        TicketEditionDto ticket = this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(TICKETS + ID_ID, "5fa45e863d6e834d642689ac")
                .exchange()
                .expectStatus().isOk()
                .expectBody(TicketEditionDto.class)
                .value(Assertions::assertNotNull)
                .value(returnTicked -> {
                    assertEquals("5fa45e863d6e834d642689ac", returnTicked.getId());
                    assertEquals(1, returnTicked.getShoppingList().get(0).getAmount());
                    assertEquals(ShoppingState.NOT_COMMITTED, returnTicked.getShoppingList().get(1).getState());
                })
                .returnResult()
                .getResponseBody();
        assertNotNull(ticket);
        List<Shopping> shoppingList = ticket.getShoppingList();
        shoppingList.get(0).setAmount(5);
        shoppingList.get(1).setState(ShoppingState.IN_STOCK);
        ticket = this.restClientTestService.loginAdmin(webTestClient)

                .put()
                .uri(TICKETS + ID_ID, "5fa45e863d6e834d642689ac")
                .bodyValue(shoppingList)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TicketEditionDto.class)
                .value(Assertions::assertNotNull)
                .value(returnTicket -> {
                    assertEquals(5, returnTicket.getShoppingList().get(0).getAmount());
                    assertEquals(ShoppingState.IN_STOCK, returnTicket.getShoppingList().get(1).getState());
                })
                .returnResult()
                .getResponseBody();
        assertNotNull(ticket);
    }

    @Test
    void testFindAllBoughtArticlesByMobileServerError() {
        this.webTestClient
                .get()
                .uri(TICKETS + TicketResource.SEARCH + BOUGHT_ARTICLES)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void testFindAllBoughtArticlesByMobile() {
        this.restClientTestService.loginCustomer(webTestClient)
                .get()
                .uri(TICKETS + TicketResource.SEARCH + BOUGHT_ARTICLES)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ArticleNewDto.class)
                .value(Assertions::assertNotNull)
                .value(articles -> assertTrue(articles.stream()
                                        .anyMatch(article ->
                                                article.getBarcode().equals("8400000000017"))));
    }

    @Test
    void testFindAllWithoutInvoice() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(TICKETS + SEARCH + NO_INVOICE)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TicketReferencesDto.class)
                .value(Assertions::assertNotNull)
                .value(ticketsRef -> assertTrue(ticketsRef.getReferences().stream()
                        .anyMatch(ticketRef -> ticketRef.equals("Asgffv521Rj6iKmzp5aERAA"))
                ));
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
