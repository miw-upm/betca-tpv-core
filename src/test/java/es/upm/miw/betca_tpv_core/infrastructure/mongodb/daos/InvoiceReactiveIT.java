package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.TestConfig;
import org.bson.types.ObjectId;
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

        String ticketId = "5fa45e863d6e834d642689ac";
        ObjectId oTickeId = new ObjectId(ticketId);
        StepVerifier
                .create(this.invoiceReactive.findByTicketIdNullSafe(oTickeId))
                .expectNextMatches(invoiceEntity -> {
                    assertEquals("invc_ID_1A2B3C4D5E", invoiceEntity.getId());
                    return true;
                })
                .thenCancel()
                .verify();
    }

    @Test
    void findByNullTicketIdNullSafe() {
        ObjectId oTickeId = null;
        StepVerifier
                .create(this.invoiceReactive.findByTicketIdNullSafe(oTickeId))
                .recordWith(ArrayList::new)
                .thenConsumeWhile(x -> true)
                .expectRecordedMatches(invoices -> !invoices.isEmpty())
                .verifyComplete();
    }
}