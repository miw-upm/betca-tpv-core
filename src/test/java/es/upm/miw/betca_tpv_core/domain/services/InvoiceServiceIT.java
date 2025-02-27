package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestConfig
public class InvoiceServiceIT {

    @Autowired
    private InvoiceService invoiceService;

    @Test
    void testCreate(){
        Shopping shopping1 = Shopping.builder().barcode("8400000000093").amount(2)
                .discount(ZERO).state(ShoppingState.COMMITTED).build();

        User user = User.builder()
                .mobile("666666000")
                .build();

        Ticket ticket = Ticket.builder()
                .id("5fa45e863d6e834d642689ac")
                .user(user)
                .shoppingList(List.of(shopping1))
                .build();

        Invoice invoice = Invoice.builder()
                .ticket(ticket)
                .user(user)
                .build();

        StepVerifier
                .create(this.invoiceService.create(invoice))
                .expectNextMatches(invoice1 -> {
                    assertNotNull(invoice1.getCreationDate());
                    assertNotNull(invoice1.getIdentity());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testGetTotalTaxes() {

        Shopping shopping1 = Shopping.builder().barcode("8400000000055").amount(1)
                .discount(ZERO).state(ShoppingState.COMMITTED).build();
        Shopping shopping2 = Shopping.builder().barcode("8400000000093").amount(1)
                .discount(BigDecimal.TEN).state(ShoppingState.NOT_COMMITTED).build();

        User user = User.builder()
                .mobile("666666000")
                .build();

        Ticket ticket = Ticket.builder()
                .id("5fa45e863d6e834d64200000")
                .user(user)
                .shoppingList(List.of(shopping1))
                .build();

        Invoice invoice = Invoice.builder()
                .ticket(ticket)
                .user(user)
                .build();

        StepVerifier
                .create(this.invoiceService.getTotalTaxes(List.of(shopping1, shopping2), invoice))
                .assertNext(result -> {
                    assertEquals(new BigDecimal("14.85"), result.getBaseTax());
                    assertEquals(new BigDecimal("3.08"), result.getTaxValue());
                })
                .verifyComplete();
    }
}