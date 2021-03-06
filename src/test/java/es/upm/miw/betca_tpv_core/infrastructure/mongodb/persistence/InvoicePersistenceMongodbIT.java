package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
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
        Ticket ticket = Ticket.builder().reference("FGhfvfMORj6iKmzp5aERAA").build();
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
    void testFindByNumber() {
        String numInvoice = "invc_N_1A2B3C4D5E";
        StepVerifier
                .create(this.invoicePersistenceMongodb.findByNumber(numInvoice))
                .expectNextMatches(dbInvoice -> {
                    assertNotNull(dbInvoice.getId());
                    assertNotNull(dbInvoice.getCreationDate());
                    assertNotNull(dbInvoice.getNumber());
                    assertEquals(numInvoice, dbInvoice.getNumber());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testFindByNumberNotExist() {
        String idInvoice = "invc_N_NOTEXIST";
        StepVerifier
                .create(this.invoicePersistenceMongodb.findByNumber(idInvoice))
                .expectError(NotFoundException.class)
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
        String ticketId = "5fa45e863d6e834d642689ac"; //

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
