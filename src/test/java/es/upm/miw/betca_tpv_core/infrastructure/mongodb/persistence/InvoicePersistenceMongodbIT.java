package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.model.Invoice;
import es.upm.miw.betca_tpv_core.domain.model.Ticket;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.ArrayList;

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

    @Test
    void testFindByPhoneNoNullAndTicketIdNullSafe(){
        String phoneUser = "666666000"; //"666666000";
        String ticketId = null; // 5fa45e863d6e834d642689ac

        StepVerifier
                .create(this.invoicePersistenceMongodb.findByPhoneAndTicketIdNullSafe(phoneUser, ticketId))
                .thenConsumeWhile(invoice -> {
                    assertNotNull(invoice.getTicket());
                    assertEquals(phoneUser, invoice.getPhoneUser());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void testFindByPhoneNullAndTicketIdNotNullSafe(){
        String phoneUser = null; //"66";
        String ticketId = "5gfaw03b7513a164chop77ac"; //

        StepVerifier
                .create(this.invoicePersistenceMongodb.findByPhoneAndTicketIdNullSafe(phoneUser, ticketId))
                .thenConsumeWhile(invoice -> {
                    assertNotNull(invoice.getTicket());
                    assertEquals(ticketId, invoice.getTicketId());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void testFindByPhoneNullAndTicketIdNullSafe(){
        String phoneUser = null; //"66";
        String ticketId = null; //

        StepVerifier
                .create(this.invoicePersistenceMongodb.findByPhoneAndTicketIdNullSafe(phoneUser, ticketId))
                .recordWith(ArrayList::new)
                .thenConsumeWhile(x -> true)
                .expectRecordedMatches(invoices -> !invoices.isEmpty())
                .verifyComplete();
    }
}
