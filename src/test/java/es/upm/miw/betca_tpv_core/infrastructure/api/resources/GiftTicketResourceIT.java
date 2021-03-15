package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.*;
import es.upm.miw.betca_tpv_core.domain.rest.UserMicroservice;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.CashierResource.CASHIERS;
import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.GiftTicketResource.GIFTTICKETS;
import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.TicketResource.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;


@RestTestConfig
public class GiftTicketResourceIT {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private RestClientTestService restClientTestService;
    @MockBean
    private UserMicroservice userMicroservice;

    @Test
    void testCreate() {
        this.restClientTestService.loginAdmin(webTestClient)
                .post().uri(CASHIERS)
                .exchange()
                .expectStatus().isOk();
        BDDMockito.given(this.userMicroservice.readByMobile(anyString()))
                .willAnswer(arguments ->
                        Mono.just(User.builder().mobile(arguments.getArgument(0)).firstName("mock").build()));

        Shopping shopping1 = Shopping.builder().barcode("8400000000017").retailPrice(new BigDecimal("20")).amount(1)
                .discount(BigDecimal.ZERO).state(ShoppingState.COMMITTED).build();
        Ticket ticket = Ticket.builder().cash(new BigDecimal("200"))
                .card(BigDecimal.ZERO).voucher(BigDecimal.ZERO).note("note")
                .shoppingList(List.of(shopping1)).user(User.builder().mobile("666666004").build()).build();
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
                    assertEquals(0, new BigDecimal("20.00").compareTo(returnTicket.total()));
                }).returnResult().getResponseBody();
        assertNotNull(dbTicket);

        GiftTicket giftTicket = GiftTicket.builder().message("description of GiftTicket")
                .ticketId(dbTicket.getId()).build();
        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(GIFTTICKETS)
                .body(Mono.just(giftTicket), GiftTicket.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(GiftTicket.class)
                .value(Assertions::assertNotNull)
                .value(returnGiftTicket -> {
                    System.out.println(">>>>> Test:: returnGiftTicket:" + returnGiftTicket);
                    assertNotNull(returnGiftTicket.getId());
                    assertEquals("description of GiftTicket", returnGiftTicket.getMessage());
                    assertEquals(dbTicket.getId(), returnGiftTicket.getTicketId());
                });
    }

    @Test
    void testCreateNotFoundTicketIdException() {
        GiftTicket giftTicket = GiftTicket.builder().message("description of GiftTicket")
                .ticketId("sfsfsfd").build();
        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(GIFTTICKETS)
                .body(Mono.just(giftTicket), GiftTicket.class)
                .exchange()
                .expectStatus().isNotFound();
    }

}
