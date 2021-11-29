package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.*;
import es.upm.miw.betca_tpv_core.domain.rest.UserMicroservice;
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
class TicketServiceIT {

    @Autowired
    private TicketService ticketService;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private CashierService cashierService;

    @MockBean
    private UserMicroservice userMicroservice;

    @BeforeEach
    void openCashier() {
        StepVerifier
                .create(this.cashierService.createOpened())
                .verifyComplete();
        BDDMockito.given(this.userMicroservice.readByMobile(any(String.class)))
                //.willReturn(Mono.just(User.builder().mobile("666666666").firstName("mock").build()))
                .willAnswer(arguments ->
                        Mono.just(User.builder().mobile(arguments.getArgument(0)).firstName("mock").build()));
    }


    @Test
    void tesCreate() {
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
        StepVerifier
                .create(this.ticketService.create(ticket))
                .expectNextMatches(dbTicket -> {
                    assertNotNull(dbTicket.getId());
                    assertNotNull(dbTicket.getCreationDate());
                    assertNotNull(dbTicket.getReference());
                    assertEquals(2, dbTicket.getShoppingList().size());
                    return true;
                })
                .expectComplete()
                .verify();

        StepVerifier
                .create(this.articleService.read("8400000000093"))
                .assertNext(article -> assertEquals(stock.get() - 3, article.getStock()))
                .verifyComplete();
    }

    @Test
    void tesCreateNotFoundException() {
        AtomicInteger stock = new AtomicInteger();
        StepVerifier
                .create(this.articleService.read("8400000000093")).consumeNextWith(article -> stock.set(article.getStock()))
                .verifyComplete();
        Shopping shopping = Shopping.builder().barcode("kk").amount(2)
                .discount(BigDecimal.TEN).state(ShoppingState.NOT_COMMITTED).build();
        Ticket ticket = Ticket.builder().cash(new BigDecimal("200"))
                .card(ZERO).voucher(ZERO).note("note")
                .shoppingList(List.of(shopping)).build();
        StepVerifier
                .create(this.ticketService.create(ticket))
                .expectError(NotFoundException.class)
                .verify();

        StepVerifier
                .create(this.articleService.read("8400000000093"))
                .assertNext(article -> assertEquals(stock.get(), article.getStock()))
                .verifyComplete();
    }

    @Test
    void testReceipt() {
        StepVerifier
                .create(this.ticketService.readReceipt("5fa45e863d6e834d642689ac"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @AfterEach
    void closeCashier() {
        StepVerifier
                .create(this.cashierService.close(new CashierClose(ZERO, ZERO, "test")))
                .consumeNextWith(System.out::println)
                .verifyComplete();
    }
}
