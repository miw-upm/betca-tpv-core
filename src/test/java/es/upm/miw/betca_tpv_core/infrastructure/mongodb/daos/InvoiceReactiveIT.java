package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestConfig
class InvoiceReactiveIT {

    @Autowired
    private InvoiceReactive invoiceReactive;

    @Test
    void findByTicketIdNullSafe() {
        String ticketId = "5gfaw03b7513a164chop77ac";
        StepVerifier
                .create(this.invoiceReactive.findByTicketIdNullSafe(ticketId))
                .expectNextMatches(invoiceEntity -> {
                    assertEquals("invc_ID_9Z8X7Y6V5U", invoiceEntity.getId());
                    return true;
                })
                .thenCancel()
                .verify();
    }

    @Test
    void findByNullTicketIdNullSafe() {
        String ticketId = null;
        StepVerifier
                .create(this.invoiceReactive.findByTicketIdNullSafe(ticketId))
                .recordWith(ArrayList::new)
                .thenConsumeWhile(x -> true)
                .expectRecordedMatches(invoices -> !invoices.isEmpty())
                .verifyComplete();
    }
}
