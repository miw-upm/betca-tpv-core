package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Ticket;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestConfig
class InvoiceServiceIT {

    @Autowired
    private InvoiceService invoiceService;

    @Test
    void tesCreate() {
        Ticket ticket = Ticket.builder().reference("FGhfvfMORj6iKmzp5aERAA")
                .build();

        StepVerifier
                .create(this.invoiceService.create(ticket.getReference()))
                .expectNextMatches(dbInvoice -> {
                    assertNotNull(dbInvoice);
                    assertNotNull(dbInvoice.getCreationDate());
                    assertNotNull(dbInvoice.getNumber());
                    assertEquals(ticket.getReference(), dbInvoice.getTicket().getReference());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testReceipt() {
        StepVerifier
                .create(this.invoiceService.print("invc_ID_1A2B3C4D5E"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void testCreateAndPrintInvoice() {
        Ticket ticket = Ticket.builder().reference("FGhfv521Rj6iKmzp5aERAA")
                .build();

        StepVerifier
                .create(this.invoiceService.createInvoiceAndPrint(ticket.getReference()))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void testPrintByNumber() {
        String numberInvoice = "invc_N_1A2B3C4D5E";

        StepVerifier
                .create(this.invoiceService.printByNumber(numberInvoice))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void testPrintByNumberNotExist() {
        String numberInvoice = "invc_NOT_EXIST";

        StepVerifier
                .create(this.invoiceService.printByNumber(numberInvoice))
                .expectError(NotFoundException.class)
                .verify();
    }


    @Test
    void testFindByPhoneNoNullAndTicketIdNullSafe(){
        String phoneUser = "666666000"; //"666666000";
        String ticketId = null; // 5fa45e863d6e834d642689ac

        StepVerifier
                .create(this.invoiceService.findByPhoneAndTicketIdNullSafe(phoneUser, ticketId))
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
        String ticketId = "5fa45f6f3a61083cb241289c"; //

        StepVerifier
                .create(this.invoiceService.findByPhoneAndTicketIdNullSafe(phoneUser, ticketId))
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
                .create(this.invoiceService.findByPhoneAndTicketIdNullSafe(phoneUser, ticketId))
                .recordWith(ArrayList::new)
                .thenConsumeWhile(x -> true)
                .expectRecordedMatches(invoices -> !invoices.isEmpty())
                .verifyComplete();
    }

    @Test
    void testFindByNumber(){
        String numberInvoice = "invc_N_1A2B3C4D5E";

        StepVerifier
                .create(this.invoiceService.findByNumber(numberInvoice))
                .thenConsumeWhile(invoice -> {
                    assertNotNull(invoice);
                    assertNotNull(invoice.getId());
                    assertNotNull(invoice.getNumber());
                    assertEquals(numberInvoice, invoice.getNumber());
                    assertNotNull(invoice.getTicket());
                    assertNotNull(invoice.getTicket().getUser());
                    assertEquals("666666000", invoice.getPhoneUser());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void testCreateFromTicketRef(){
        String ticketRef = "FGhfv521Rj6iKmzp5aERAA";
        StepVerifier
                .create(this.invoiceService.createFromTicketRef(ticketRef))
                .thenConsumeWhile(invoice -> {
                    assertNotNull(invoice);
                    assertNotNull(invoice.getId());
                    assertNotNull(invoice.getNumber());
                    assertNotNull(invoice.getTicket());
                    assertNotNull(invoice.getTicket().getUser());
                    assertEquals(ticketRef, invoice.getTicket().getReference());
                    assertEquals("66", invoice.getPhoneUser());
                    return true;
                })
                .verifyComplete();
    }
}
