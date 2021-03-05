package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestConfig
public class TicketReactiveIT {

    @Autowired
    private TicketReactive ticketReactive;

    @Test
    void testFindByReference() {
        StepVerifier
                .create(this.ticketReactive.findByReference("nUs81zZ4R_iuoq0_zCRm6A"))
                .expectNextMatches(ticket -> {
                    assertEquals("5fa45e863d6e834d642689ac", ticket.getId());
                    return true;
                })
                .thenCancel()
                .verify();
    }
}
