package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

@TestConfig
class ProviderInvoicePersistenceMongodbIT {

    @Autowired
    private ProviderInvoicePersistenceMongodb providerInvoicePersistenceMongodb;

    @Test
    void testFindAll() {
        StepVerifier
                .create(this.providerInvoicePersistenceMongodb.findAll())
                .expectNextMatches(providerInvoice -> {
                    System.out.println(providerInvoice);
                    assertNotNull(providerInvoice.getNumber());
                    return true;
                })
                .thenCancel()
                .verify();
    }
}