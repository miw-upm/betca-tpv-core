package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.model.Invoice;
import es.upm.miw.betca_tpv_core.domain.model.Ticket;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestConfig
class InvoicePersistenceMongodbIT {

    @Autowired
    private InvoicePersistenceMongodb invoicePersistenceMongodb;

    @Test
    void tesCreate() {
        Ticket ticket = Ticket.builder().reference("nUs81zZ4R_iuoq0_zCRm6A").build();
        Invoice invoice = Invoice.builder().number("01A2B3C4D5E").creationDate(LocalDateTime.now()).ticket(ticket).build();
        StepVerifier
                .create(this.invoicePersistenceMongodb.create(invoice))
                .expectNextMatches(dbInvoice -> {
                    assertNotNull(dbInvoice.getId());
                    assertNotNull(dbInvoice.getCreationDate());
                    assertEquals(invoice.getNumber(), dbInvoice.getNumber());
                    assertEquals(invoice.getTicket().getReference(), dbInvoice.getTicket().getReference());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testFindBydId() {
        String idInvoice = "invc_ID_1A2B3C4D5E";
        StepVerifier
                .create(this.invoicePersistenceMongodb.findById(idInvoice))
                .expectNextMatches(dbInvoice -> {
                    assertNotNull(dbInvoice.getId());
                    assertNotNull(dbInvoice.getCreationDate());
                    assertNotNull(dbInvoice.getNumber());
                    return true;
                })
                .expectComplete()
                .verify();
    }
}
