package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Invoice;
import es.upm.miw.betca_tpv_core.domain.model.Shopping;
import es.upm.miw.betca_tpv_core.domain.model.Ticket;
import es.upm.miw.betca_tpv_core.domain.model.User;
import es.upm.miw.betca_tpv_core.domain.rest.UserMicroservice;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.InvoiceResource.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@RestTestConfig
public class InvoiceResourceIT {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private RestClientTestService restClientTestService;
    @MockBean
    private UserMicroservice userMicroservice;

    @BeforeEach
    void onStartMobileForUser() {
        BDDMockito.given(this.userMicroservice.readByMobile(any(String.class)))
                .willAnswer(arguments -> Mono.just(User.builder().mobile(arguments.getArgument(0)).build()));
    }

    @Test
    void testCreate(){
        User user = User.builder()
                .mobile("666666005")
                .build();

        Ticket ticket = Ticket.builder()
                .id("5fa4608f4928694ef5980e4d")
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
                    assertEquals(new BigDecimal("0.01"), returnInvoice.getBaseTax());
                    assertEquals(new BigDecimal("0.00"), returnInvoice.getTaxValue());
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

    @Test
    void testRead(){
        Invoice invoice = this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(INVOICES + IDENTITY_ID, 20253)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Invoice.class)
                .value(Assertions::assertNotNull)
                .value(invoice1 -> {
                    assertEquals(20253, invoice1.getIdentity());
                    assertNotNull(invoice1.getTicket());
                })
                .returnResult()
                .getResponseBody();
        assertNotNull(invoice);
    }

    @Test
    void testReceipt(){
        this.restClientTestService.loginAdmin(webTestClient)
                .get().uri(INVOICES + IDENTITY_ID + RECEIPT, 20252)
                .exchange()
                .expectStatus().isOk()
                .expectBody(byte[].class)
                .value(Assertions::assertNotNull);
    }

    @Test
    void testFindByTicketId(){
        this.restClientTestService.loginAdmin(webTestClient)
                .get().uri(uriBuilder -> uriBuilder.path(INVOICES + TICKET_SEARCH)
                        .queryParam("ticketId", "5fa4603b7513a164c99677ac").build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Invoice.class)
                .value(Assertions::assertNotNull);
    }

    @Test
    void testFindByUserMobile() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(INVOICES + MOBILE_SEARCH)
                        .queryParam("mobile", "666666004")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Invoice.class)
                .value(Assertions::assertNotNull)
                .value(invoices -> assertTrue(invoices
                        .stream().allMatch(invoice ->
                                invoice.getIdentity().equals(20252))));
    }

    @Test
    void testUpdateUser(){
        this.restClientTestService.loginAdmin(webTestClient)
                .patch()
                .uri(INVOICES + "/{identity}", 20253).contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(User.builder().mobile("600000012").build()), User.class).exchange()
                .expectStatus().isOk();
        Invoice invoice = this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(INVOICES + IDENTITY_ID, 20253).exchange()
                .expectStatus().isOk()
                .expectBody(Invoice.class).value(Assertions::assertNotNull)
                .value(invoice1 -> {
                    assertNotNull(invoice1);
                    assertEquals("600000012", invoice1.getUser().getMobile());
                    assertEquals(20253, invoice1.getIdentity());})
                .returnResult().getResponseBody();
        assertNotNull(invoice);
    }
}