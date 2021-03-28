package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

@TestConfig
public class ProviderInvoiceReactiveIT {

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
                .thenCancel()
                .verify();
    }
}
