package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@TestConfig
class ProviderInvoiceReactiveIT {

    @Autowired
    private ProviderInvoiceReactive providerInvoiceReactive;

    @Test
    void testFindByNumber() {
        StepVerifier
                .create(this.providerInvoiceReactive.findByNumber(1111))
                .expectNextMatches(providerInvoiceEntity -> {
                    assertEquals("1", providerInvoiceEntity.getId());
                    assertEquals(1111, providerInvoiceEntity.getNumber());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void testFindByCreationDateBetweenInclusive() {
        LocalDate startDate = LocalDate.of(2020, 1, 1);
        LocalDate endDate = LocalDate.of(2021, 1, 31);
        StepVerifier
                .create(this.providerInvoiceReactive.findByCreationDateBetweenInclusive(startDate, endDate))
                .expectNextMatches(providerInvoiceEntity -> {
                    assertEquals("1", providerInvoiceEntity.getId());
                    assertEquals(1111, providerInvoiceEntity.getNumber());
                    return true;
                })
                .thenCancel()
                .verify();
    }
}
