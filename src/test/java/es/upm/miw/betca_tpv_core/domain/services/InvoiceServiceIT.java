package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.model.Ticket;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestConfig
class InvoiceServiceIT {

    @Autowired
    private InvoiceService invoiceService;

    @Test
    void tesCreate() {
        Ticket ticket = Ticket.builder().reference("nUs81zZ4R_iuoq0_zCRm6A")
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
        Ticket ticket = Ticket.builder().reference("nUs81zZ4R_iuoq0_zCRm6A")
                .build();

        StepVerifier
                .create(this.invoiceService.createInvoiceAndPrint(ticket.getReference()))
                .expectNextCount(1)
                .verifyComplete();
    }
}
