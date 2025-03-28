package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.*;
import es.upm.miw.betca_tpv_core.domain.rest.UserMicroservice;
import es.upm.miw.betca_tpv_core.domain.services.CustomerPointsService;
import es.upm.miw.betca_tpv_core.domain.services.SlackService;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.GiftTicketDto;
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
import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.GiftTicketResource.GIFTTICKETS;
import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.GiftTicketResource.REFERENCE_GIFTTICKETS;
import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.TicketResource.*;
import static java.math.BigDecimal.ZERO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@RestTestConfig
class GiftTicketResourceIT {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private RestClientTestService restClientTestService;
    @MockBean
    private CustomerPointsService customerPointsService;
    @MockBean
    private UserMicroservice userMicroservice;
    @MockBean
    private SlackService slackService;

    @BeforeEach
    void openCashier() {
        BDDMockito.doNothing().when(this.slackService).sendMessage(any(),any());
        this.restClientTestService.loginAdmin(webTestClient)
                .post().uri(CASHIERS)
                .exchange()
                .expectStatus().isOk();
        BDDMockito.given(this.customerPointsService.readCustomerPointsByMobile(anyString()))
                .willAnswer(arguments -> {
                    String mobile = arguments.getArgument(0);
                    User user = User.builder().mobile(mobile).build();
                    return Mono.just(CustomerPoints.builder().user(user).value(100).build());
                });
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
        /*POST ticket*/
        Ticket dbTicket = this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(TICKETS)
                .body(Mono.just(ticket), Ticket.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Ticket.class)
                .value(Assertions::assertNotNull)
                .value(returnTicket -> {
                    assertNotNull(returnTicket.getId());
                    assertNotNull(returnTicket.getReference());
                    assertNotNull(returnTicket.getCreationDate());
                    assertEquals(0, new BigDecimal("95.06").compareTo(returnTicket.total()));
                }).returnResult().getResponseBody();
        assertNotNull(dbTicket);
        /*GET ticket*/
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(TICKETS + ID_ID + RECEIPT, dbTicket.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(byte[].class)
                .value(Assertions::assertNotNull);
        //GiftTicket giftTicket = new GiftTicket("mensajeuno", ticket);
        GiftTicketDto giftTicketDto = new GiftTicketDto(dbTicket.getId(), "mensajeUno");
        /*POST gift ticket*/
        GiftTicket giftTicket = this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(GIFTTICKETS)
                .body(Mono.just(giftTicketDto), GiftTicketDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(GiftTicket.class)
                .value(Assertions::assertNotNull)
                .value(returnGiftTicket -> {
                    assertNotNull(returnGiftTicket.getId());
                    assertNotNull(returnGiftTicket.getReference());
                    assertNotNull(returnGiftTicket.getMessage());
                    assertNotNull(returnGiftTicket.getTicket());
                    assertNotNull(returnGiftTicket.getTicket().getShoppingList().get(0));
                    assertNotNull(returnGiftTicket.getTicket().getShoppingList().get(1));
                }).returnResult().getResponseBody();
        assertNotNull(giftTicket);
        /*GET gift ticket*/
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(GIFTTICKETS + REFERENCE_GIFTTICKETS + RECEIPT, giftTicket.getReference())
                .exchange()
                .expectStatus().isOk()
                .expectBody(byte[].class)
                .value(Assertions::assertNotNull);
    }

    @Test
    void testCreateNotFound() {
        Shopping shopping1 = Shopping.builder().barcode("8400000000017").retailPrice(new BigDecimal("20")).amount(1)
                .discount(BigDecimal.ZERO).state(ShoppingState.COMMITTED).build();
        Shopping shopping2 = Shopping.builder().barcode("8400000000024").retailPrice(new BigDecimal("27.8")).amount(3)
                .discount(BigDecimal.TEN).state(ShoppingState.NOT_COMMITTED).build();
        Ticket ticket = Ticket.builder().cash(new BigDecimal("200"))
                .card(BigDecimal.ZERO).voucher(BigDecimal.ZERO).note("note")
                .shoppingList(List.of(shopping1, shopping2)).user(User.builder().mobile("666666004").build()).build();
        /*POST ticket*/
        Ticket dbTicket = this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(TICKETS)
                .body(Mono.just(ticket), Ticket.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Ticket.class)
                .value(Assertions::assertNotNull)
                .value(returnTicket -> {
                    assertNotNull(returnTicket.getId());
                    assertNotNull(returnTicket.getReference());
                    assertNotNull(returnTicket.getCreationDate());
                    assertEquals(0, new BigDecimal("95.06").compareTo(returnTicket.total()));
                }).returnResult().getResponseBody();
        assertNotNull(dbTicket);
        /*GET ticket*/
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(TICKETS + ID_ID + RECEIPT, dbTicket.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(byte[].class)
                .value(Assertions::assertNotNull);
        GiftTicketDto giftTicketDto = new GiftTicketDto("0", "mensajeUno");
        /*POST gift ticket*/
        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(GIFTTICKETS)
                .body(Mono.just(giftTicketDto), GiftTicketDto.class)
                .exchange()
                .expectStatus().isNotFound();
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