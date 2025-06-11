package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.model.*;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.InvoiceDTO;
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
                    assertEquals(new BigDecimal("3.08"), result.getTaxValue());
                })
                .verifyComplete();
    }

    @Test
    void testReceipt(){
        StepVerifier.create(this.invoiceService.readReceipt(20251,"")).expectNextCount(1).verifyComplete();
    }

    @Test
    void testFindByTicketId(){
        StepVerifier
                .create(this.invoiceService.findByTicketId("5fa45e863d6e834d642689ac"))
                .expectNextMatches(invoice1 ->{
                    assertEquals("5fa45e863d6e834d642689ac", invoice1.getTicket().getId());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testFindByUserMobile() {
        StepVerifier
                .create(this.invoiceService.findByUserMobile("666666000"))
                .expectNextMatches(invoice1 -> {
                    assertEquals("666666000", invoice1.getUser().getMobile());
                    return true;
                })
                .expectComplete()
                .verify();
    }
}