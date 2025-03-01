package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestConfig
public class InvoiceReactiveIT {
    @Autowired
    private InvoiceReactive invoiceReactive;

    @Test
    void testTopByOrderByIdentityDesc() {
        StepVerifier
                .create(this.invoiceReactive.findTopByOrderByIdentityDesc())
                .assertNext(invoice ->
                        assertTrue(invoice.getIdentity()
                                .toString()
                                .contains(Integer.toString(LocalDate.now().getYear()))))
                .thenCancel()
                .verify();
    }

    @Test
    void testFindByIdentity() {
        StepVerifier
                .create(this.invoiceReactive.findByIdentity(20252))
                .assertNext(invoice -> assertEquals(20252, (int) invoice.getIdentity()))
                .thenCancel()
                .verify();
    }

    @Test
    void testFindByTicketId(){
        StepVerifier
                .create(this.invoiceReactive.findByTicketId("5fa45e863d6e834d642689ac"))
                .assertNext(invoice -> assertTrue(invoice.getTicketId().contains("5fa45e863d6e834d642689ac")))
                .thenCancel()
                .verify();
    }

    @Test
    void testFindByUserMobile() {
        StepVerifier
                .create(this.invoiceReactive.findByUserMobile("666666004"))
                .assertNext(invoice -> assertTrue(invoice.getUserMobile().contains("666666004")))
                .thenCancel()
                .verify();
    }
}