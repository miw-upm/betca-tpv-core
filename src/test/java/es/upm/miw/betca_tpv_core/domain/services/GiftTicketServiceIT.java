package es.upm.miw.betca_tpv_core.domain.services;

import com.mongodb.assertions.Assertions;
import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.model.*;
import es.upm.miw.betca_tpv_core.domain.rest.UserMicroservice;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.GiftTicketDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.math.BigDecimal.ZERO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

@TestConfig
class GiftTicketServiceIT {

    @Autowired
    private TicketService ticketService;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private CashierService cashierService;
    @Autowired
    private GiftTicketService giftTicketService;

    @MockBean
    private UserMicroservice userMicroservice;

    @MockBean
    private SlackService slackService;

    @BeforeEach
    void openCashier() {
        StepVerifier
                .create(this.cashierService.createOpened())
                .verifyComplete();
        BDDMockito.given(this.userMicroservice.readByMobile(any(String.class)))
                .willAnswer(arguments ->
                        Mono.just(User.builder().mobile(arguments.getArgument(0)).firstName("mock").build()));
        BDDMockito.doNothing().when(this.slackService).sendMessage(any(),any());
    }

    @Test
    void testCreateGiftTicket() {
        AtomicInteger stock = new AtomicInteger();
        StepVerifier
                .create(this.articleService.read("8400000000093")).consumeNextWith(article -> stock.set(article.getStock()))
                .verifyComplete();
        Shopping shopping1 = Shopping.builder().barcode("8400000000093").amount(1)
                .discount(ZERO).state(ShoppingState.COMMITTED).build();
        Shopping shopping2 = Shopping.builder().barcode("8400000000093").amount(2)
                .discount(BigDecimal.TEN).state(ShoppingState.NOT_COMMITTED).build();
        Ticket ticket = Ticket.builder().cash(new BigDecimal("200")).card(ZERO).voucher(ZERO)
                .user(User.builder().mobile("666666004").build()).note("note")
                .shoppingList(List.of(shopping1, shopping2)).build();
        Ticket ticketTest = this.ticketService.create(ticket).block();
        assertNotNull(ticketTest);
        assertNotNull(ticketTest.getId());
        assertNotNull(ticketTest.getCreationDate());
        assertNotNull(ticketTest.getCard());
        assertNotNull(ticketTest.getVoucher());
        assertNotNull(ticketTest.getNote());

        GiftTicketDto giftTicketDto = new GiftTicketDto(ticketTest.getId(), "feliz cumpleaños");

        StepVerifier
                .create(this.giftTicketService.create(giftTicketDto))
                .expectNextMatches(dbTicket -> {
                    assertEquals(dbTicket.getTicket().getId(), giftTicketDto.getId());
                    assertEquals(dbTicket.getMessage(), giftTicketDto.getMessage());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void tesReadForReceiptByReference() {
        AtomicInteger stock = new AtomicInteger();
        StepVerifier
                .create(this.articleService.read("8400000000093")).consumeNextWith(article -> stock.set(article.getStock()))
                .verifyComplete();
        Shopping shopping1 = Shopping.builder().barcode("8400000000093").amount(1)
                .discount(ZERO).state(ShoppingState.COMMITTED).build();
        Shopping shopping2 = Shopping.builder().barcode("8400000000093").amount(2)
                .discount(BigDecimal.TEN).state(ShoppingState.NOT_COMMITTED).build();
        Ticket ticket = Ticket.builder().cash(new BigDecimal("200")).card(ZERO).voucher(ZERO)
                .user(User.builder().mobile("666666004").build()).note("note")
                .shoppingList(List.of(shopping1, shopping2)).build();
        Ticket ticketTest = this.ticketService.create(ticket).block();
        assertNotNull(ticketTest);
        assertNotNull(ticketTest.getId());
        assertNotNull(ticketTest.getCreationDate());
        assertNotNull(ticketTest.getCard());
        assertNotNull(ticketTest.getVoucher());
        assertNotNull(ticketTest.getNote());

        GiftTicketDto giftTicketDto = new GiftTicketDto(ticketTest.getId(), "feliz cumpleaños");
        GiftTicket giftTicketTest = this.giftTicketService.create(giftTicketDto).block();
        assertNotNull(giftTicketTest);
        assertNotNull(giftTicketTest.getReference());

        StepVerifier
                .create(this.giftTicketService.readForReceiptByReference(giftTicketTest.getReference()))
                .assertNext(Assertions::assertNotNull)
                .verifyComplete();
    }

    @AfterEach
    void closeCashier() {
        StepVerifier
                .create(this.cashierService.close(new CashierClose(ZERO, ZERO, "test")))
                .expectNextCount(1)
                .verifyComplete();
    }
}
