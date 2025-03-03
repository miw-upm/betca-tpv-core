package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestConfig
class InvoicePersistenceMongodbIT {

    @Autowired
    private InvoicePersistenceMongodb invoicePersistenceMongodb;

    @Test
    void tesCreate() {
        Shopping shopping1 = Shopping.builder().barcode("8400000000093").amount(1)
                .discount(ZERO).state(ShoppingState.COMMITTED).build();

        LocalDateTime date = LocalDateTime.now();

        User user = User.builder()
                .mobile("666666004")
                .build();

        Ticket ticket = Ticket.builder()
                .id("5fa45e863d6e834d642689ac")
                .user(user)
                .shoppingList(List.of(shopping1))
                .build();

        Invoice invoice = Invoice.builder()
                .ticket(ticket)
                .creationDate(date)
                .user(user)
                .build();
        StepVerifier
                .create(this.invoicePersistenceMongodb.create(invoice))
                .expectNextMatches(invoice1 -> {
                    assertEquals(date, invoice1.getCreationDate());
                    assertNotNull(invoice1.getIdentity());
                    return true;})
                .expectComplete()
                .verify();
    }

    @Test
    void testFindByIdentity() {
        StepVerifier
                .create(this.invoicePersistenceMongodb.readByIdentity(20252))
                .expectNextMatches(invoice1 -> {
                    assertEquals(20252, invoice1.getIdentity());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testFindByTicketId() {
        StepVerifier
                .create(this.invoicePersistenceMongodb.findByTicketId("5fa45e863d6e834d642689ac"))
                .expectNextMatches(invoice1 -> {
                    assertEquals("5fa45e863d6e834d642689ac", invoice1.getTicket().getId());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testFindByUserMobile() {
        StepVerifier
                .create(this.invoicePersistenceMongodb.findByUserMobile("666666004"))
                .expectNextMatches(invoice1 -> {
                    assertEquals("666666004", invoice1.getUser().getMobile());
                    return true;
                })
                .expectComplete()
                .verify();
    }
}