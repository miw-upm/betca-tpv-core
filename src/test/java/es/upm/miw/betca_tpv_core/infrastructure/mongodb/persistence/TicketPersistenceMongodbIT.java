package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.model.Shopping;
import es.upm.miw.betca_tpv_core.domain.model.ShoppingState;
import es.upm.miw.betca_tpv_core.domain.model.Ticket;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestConfig
class TicketPersistenceMongodbIT {

    @Autowired
    private TicketPersistenceMongodb ticketPersistenceMongodb;

    @Test
    void tesCreate() {
        Shopping shopping1 = Shopping.builder().barcode("8400000000017").amount(2)
                .discount(BigDecimal.ZERO).state(ShoppingState.COMMITTED).build();
        Shopping shopping2 = Shopping.builder().barcode("8400000000024").amount(3)
                .discount(BigDecimal.TEN).state(ShoppingState.NOT_COMMITTED).build();
        Ticket ticket = Ticket.builder().reference("RyR_8_SkT9igCikrWWWGkQ").cash(new BigDecimal("200"))
                .card(BigDecimal.ZERO).voucher(BigDecimal.ZERO).note("note").creationDate(LocalDateTime.now())
                .shoppingList(List.of(shopping1, shopping2)).build();
        StepVerifier
                .create(this.ticketPersistenceMongodb.create(ticket))
                .expectNextMatches(dbTicket -> {
                    assertNotNull(dbTicket.getId());
                    assertNotNull(dbTicket.getCreationDate());
                    assertEquals("RyR_8_SkT9igCikrWWWGkQ", dbTicket.getReference());
                    assertEquals(2, dbTicket.getShoppingList().size());
                    return true;
                })
                .expectComplete()
                .verify();
    }

}
