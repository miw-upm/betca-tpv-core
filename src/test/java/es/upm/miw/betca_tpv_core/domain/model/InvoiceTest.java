package es.upm.miw.betca_tpv_core.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InvoiceTest {
    @Test

    void testBuilder(){
        LocalDateTime now = LocalDateTime.now();
        BigDecimal taxValue = new BigDecimal("16.2");
        BigDecimal baseTax = new BigDecimal("13.4");
        User user = User.builder()
                .mobile("622547896")
                .build();

        Ticket ticket = Ticket.builder()
                .reference("lahaH24asd_FAskasErDAKas")
                .creationDate(now)
                .user(user)
                .build();

        Invoice invoice = Invoice.builder()
                .creationDate(now)
                .taxValue(taxValue)
                .baseTax(baseTax)
                .ticket(ticket)
                .user(user)
                .build();

        assertEquals(user, invoice.getUser());
        assertEquals(ticket, invoice.getTicket());
        assertEquals(now, invoice.getCreationDate());
        assertEquals(taxValue, invoice.getTaxValue());
        assertEquals(baseTax, invoice.getBaseTax());
    }
}